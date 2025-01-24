package cn.cisdigital.datakits.framework.util.qbeeopensdk.service;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import im.qingtui.qbee.open.platfrom.base.model.vo.base.BaseList;
import im.qingtui.qbee.open.platfrom.portal.model.param.employee.EmployeeListUserIdParam;
import im.qingtui.qbee.open.platfrom.portal.model.vo.employee.EmployeeBaseInfo;
import im.qingtui.qbee.open.platfrom.portal.model.vo.employee.EmployeeInfo;
import im.qingtui.qbee.open.platfrom.portal.model.vo.employee.EmployeeOrgInfo;
import im.qingtui.qbee.open.platfrom.portal.model.vo.employee.EmployeeUserInfo;
import im.qingtui.qbee.open.platfrom.portal.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xxx
 * @since 2024-03-12
 */
@Slf4j
@Component
public class PlatformEmployeeManager {

    /**
     * key employeeId  value 用户信息
     */
    private static LoadingCache<String, Optional<EmployeeInfo>> EMPLOYEE_INFO_CACHE;
    /**
     * key userId  value employeeId 用于通过userId找人
     */
    private static LoadingCache<String, Optional<String>> USER_ID_MAP_EMPLOYEE_ID_CACHE;
    /**
     * key account员工账号  value employeeId 用于通过员工账号找人
     */
    private static LoadingCache<String, Optional<String>> ACCOUNT_EMPLOYEE_INFO_CACHE;
    /**
     * 缓存强制过期时间 默认5分钟
     */
    @Value("${public.qbee-open-platform.cache-timeout-second:300}")
    private int qbeeCacheTimeoutSecond;


    @PostConstruct
    public void init() {
        log.info("qbee开放接口请求缓存过期时长: {} 秒", qbeeCacheTimeoutSecond);
        EMPLOYEE_INFO_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(qbeeCacheTimeoutSecond, TimeUnit.SECONDS)
            .build(
                new CacheLoader<String, Optional<EmployeeInfo>>() {
                    @Nonnull
                    @Override
                    public Optional<EmployeeInfo> load(@Nonnull String employId) {
                        try {
                            return addCache(employId);
                        } catch (Exception e) {
                            log.error("请求用户信息失败：{}", employId, e);
                        }
                        return Optional.empty();
                    }
                });

        USER_ID_MAP_EMPLOYEE_ID_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(qbeeCacheTimeoutSecond, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Optional<String>>() {
                @Nonnull
                @Override
                public Optional<String> load(@Nonnull String userId) {
                    try {
                        return addUserIdMapCache(userId);
                    } catch (Exception e) {
                        log.error("请求用户userId信息失败：{}", userId, e);
                    }
                    return Optional.empty();
                }
            });

        ACCOUNT_EMPLOYEE_INFO_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(qbeeCacheTimeoutSecond, TimeUnit.SECONDS)
            .build(
                new CacheLoader<String, Optional<String>>() {
                    @Nonnull
                    @Override
                    public Optional<String> load(@Nonnull String account) {
                        try {
                            return addAccountCache(account);
                        } catch (Exception e) {
                            log.error("请求用户信息失败, 账号：{}", account, e);
                        }
                        return Optional.empty();
                    }
                });
    }

    /**
     * 按employeeId获取人员
     */
    protected static Optional<EmployeeInfo> getEmployeeById(String employeeId) {
        try {
            return EMPLOYEE_INFO_CACHE.get(employeeId);
        } catch (ExecutionException e) {
            log.error("获取用户信息异常, employeeId:{}", employeeId, e);
        }
        return Optional.empty();
    }

    /**
     * 按employeeIds批量获取人员
     */
    protected static Map<String, Optional<EmployeeInfo>> getEmployeeByIds(Collection<String> employeeIds) {
        Map<String, Optional<EmployeeInfo>> result = new HashMap<>(0);
        if (CollectionUtils.isEmpty(employeeIds)) {
            return result;
        }
        //需要批量加入缓存
        List<String> noCacheList = employeeIds.stream()
            .filter(employeeId -> StringUtils.hasText(employeeId) && Objects.isNull(EMPLOYEE_INFO_CACHE.getIfPresent(employeeId)))
            .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(noCacheList)) {
            addCacheBatch(noCacheList);
        }
        for (String employeeId : employeeIds) {
            //兼容查询不出的错误
            Optional<EmployeeInfo> employeeDto;
            try {
                employeeDto = EMPLOYEE_INFO_CACHE.get(employeeId);
            } catch (Exception e) {
                log.error("获取用户信息异常, employeeId:{}", employeeId, e);
                employeeDto = Optional.empty();
            }
            result.put(employeeId, employeeDto);
        }
        return result;
    }


    /**
     * 查询用户的拥有的所有组织机构id
     *
     * @param employId
     * @return
     */
    protected static List<String> getAllOrgByEmployId(String employId) throws Exception {
        List<String> result = new ArrayList<>();
        BaseList<EmployeeOrgInfo> employeeOrgInfoList = EmployeeService.getEmployeeOrgInfoList(employId);
        List<EmployeeOrgInfo> list = employeeOrgInfoList.getList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        return list.stream().map(EmployeeOrgInfo::getId).collect(Collectors.toList());
    }

    /**
     * 通过userId获取用户信息
     *
     * @param userId
     * @return
     */
    protected static Optional<EmployeeInfo> getEmployeeByUserId(String userId) {
        try {
            Optional<String> employId = USER_ID_MAP_EMPLOYEE_ID_CACHE.get(userId);
            if (employId.isPresent()) {
                return EMPLOYEE_INFO_CACHE.get(employId.get());
            } else {
                return Optional.empty();
            }
        } catch (ExecutionException e) {
            log.error("获取用户信息异常, employeeId:{}", userId, e);
        }
        return Optional.empty();
    }

    /**
     * 通过userId批量获取用户EmployeeID映射
     *
     * @param userIds
     * @return
     */
    protected static Map<String, Optional<EmployeeInfo>> getEmployeeByUserIds(Collection<String> userIds) {
        Map<String, Optional<EmployeeInfo>> result = new HashMap<>(0);
        if (CollectionUtils.isEmpty(userIds)) {
            return result;
        }
        //需要批量加入缓存
        List<String> noCacheList = userIds.stream().distinct()
            .filter(userId -> StringUtils.hasText(userId) && Objects.isNull(USER_ID_MAP_EMPLOYEE_ID_CACHE.getIfPresent(userId)))
            .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(noCacheList)) {
            addUserIdMapCacheBatch(noCacheList);
        }
        List<String> employIdList = new ArrayList<>();
        for (String userId : userIds) {
            //先查询userId对应的employeeId
            try {
                Optional<String> employId = USER_ID_MAP_EMPLOYEE_ID_CACHE.get(userId);
                if (employId.isPresent()) {
                    employIdList.add(employId.get());
                }
            } catch (Exception e) {
                log.error("获取用户信息异常, userId:{}", userId, e);
            }
        }
        //批量刷新EMPLOYEE_INFO_CACHE缓存
        getEmployeeByIds(employIdList);
        for (String userId : userIds) {
            //先查询userId对应的employeeId
            try {
                Optional<String> employId = USER_ID_MAP_EMPLOYEE_ID_CACHE.get(userId);
                if (employId.isPresent()) {
                    Optional<EmployeeInfo> employeeInfo = EMPLOYEE_INFO_CACHE.get(employId.get());
                    result.put(userId, employeeInfo);
                } else {
                    log.error("获取用户信息异常, userId:{}", userId);
                    result.put(userId, Optional.empty());
                }
            } catch (Exception e) {
                log.error("获取用户信息异常, userId:{}", userId, e);
                result.put(userId, Optional.empty());
            }
        }
        return result;
    }

    /**
     * 通过account获取用户信息
     *
     * @param account
     * @return
     */
    protected static Optional<EmployeeInfo> getEmployeeByAccount(String account) {
        try {
            Optional<String> employId = ACCOUNT_EMPLOYEE_INFO_CACHE.get(account);
            if (employId.isPresent()) {
                return EMPLOYEE_INFO_CACHE.get(employId.get());
            } else {
                return Optional.empty();
            }
        } catch (ExecutionException e) {
            log.error("获取用户信息异常, account:{}", account, e);
        }
        return Optional.empty();
    }

    /**
     * 通过account批量获取用户EmployeeID映射
     *
     * @param accountList
     * @return
     */
    protected static Map<String, Optional<EmployeeInfo>> getEmployeeByAccountBatch(Collection<String> accountList) {
        Map<String, Optional<EmployeeInfo>> result = new HashMap<>(0);
        if (CollectionUtils.isEmpty(accountList)) {
            return result;
        }
        //需要批量加入缓存
        List<String> noCacheList = accountList.stream().distinct()
            .filter(userId -> StringUtils.hasText(userId) && Objects.isNull(ACCOUNT_EMPLOYEE_INFO_CACHE.getIfPresent(userId)))
            .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(noCacheList)) {
            addAccountCacheBatch(noCacheList);
        }
        List<String> employIdList = new ArrayList<>();
        for (String account : accountList) {
            //先查询account对应的employeeId
            try {
                Optional<String> employId = ACCOUNT_EMPLOYEE_INFO_CACHE.get(account);
                if (employId.isPresent()) {
                    employIdList.add(employId.get());
                }
            } catch (Exception e) {
                log.error("获取用户信息异常, account:{}", account, e);
            }
        }
        //批量刷新EMPLOYEE_INFO_CACHE缓存
        getEmployeeByIds(employIdList);
        for (String account : accountList) {
            //先查询account对应的employeeId
            try {
                Optional<String> employId = ACCOUNT_EMPLOYEE_INFO_CACHE.get(account);
                if (employId.isPresent()) {
                    Optional<EmployeeInfo> employeeInfo = EMPLOYEE_INFO_CACHE.get(employId.get());
                    result.put(account, employeeInfo);
                } else {
                    log.error("获取用户信息异常, account:{}", account);
                    result.put(account, Optional.empty());
                }
            } catch (Exception e) {
                log.error("获取用户信息异常, account:{}", account, e);
                result.put(account, Optional.empty());
            }
        }
        return result;
    }

    /**
     * Qbee 获取Employee信息
     */
    private static Optional<EmployeeInfo> addCache(String employId) {
        EmployeeInfo data = null;
        try {
            data = EmployeeService.getEmployeeById(employId).getData();
        } catch (Exception e) {
            log.error("平台查询用户信息异常，employId = {} ", employId, e);
            return Optional.empty();
        }
        //处理轻推的接口bug#用户被删除后,返回的信息中没有id
        if (data == null || data.getId() == null) {
            return Optional.empty();
        }
        //同时对ACCOUNT_EMPLOYEE_INFO_CACHE更新
        if(StringUtils.hasText(data.getAccount())){
            ACCOUNT_EMPLOYEE_INFO_CACHE.put(data.getAccount(), Optional.ofNullable(data.getId()));
        }
        return Optional.of(data);
    }

    /**
     * Qbee没有提供批量可以查询 account信息和主组织机构的接口
     *
     * @param employIds
     */
    private static void addCacheBatch(Collection<String> employIds) {
        if (CollectionUtils.isEmpty(employIds)) {
            return;
        }
        List<EmployeeInfo> employeeInfoList = new ArrayList<>();
        try {
            employeeInfoList = EmployeeService.batchEmployeeById(new ArrayList<>(employIds));
        } catch (Exception e) {
            log.error("批量查询平台用户异常，可能平台接口不兼容，需要降低版本，目前采用单个循环查询", e);
            for (String employId : employIds) {
                Optional<EmployeeInfo> employeeInfo = addCache(employId);
                EMPLOYEE_INFO_CACHE.put(employId, employeeInfo);
            }
            return;
        }
        for (EmployeeInfo employ : employeeInfoList) {
            EMPLOYEE_INFO_CACHE.put(employ.getId(), Optional.of(employ));
            if(StringUtils.hasText(employ.getAccount())){
                ACCOUNT_EMPLOYEE_INFO_CACHE.put(employ.getAccount(), Optional.ofNullable(employ.getId()));
            }
        }
    }

    private static Optional<String> addAccountCache(String account){
        String employeeId = null;
        try {
            List<EmployeeBaseInfo> employeeBaseInfos = EmployeeService.listByAccounts(Collections.singletonList(account));
            if(CollectionUtils.isEmpty(employeeBaseInfos)){
                log.error("通过account未能查询到用户信息，account = {} ", account);
                return Optional.empty();
            }
            employeeId = employeeBaseInfos.get(0).getId();
        } catch (Exception e) {
            log.error("通过account未能查询到用户信息，account = {}", account, e);
            return Optional.empty();
        }
        //listByAccounts方法没有返回主组织机构  所以需要通过id重新查询
        return Optional.ofNullable(employeeId);
    }

    private static void addAccountCacheBatch(Collection<String> accounts){
        if (CollectionUtils.isEmpty(accounts)) {
            return;
        }
        List<EmployeeBaseInfo> employeeBaseInfoList = new ArrayList<>();
        try {
            employeeBaseInfoList = EmployeeService.listByAccounts(new ArrayList<>(accounts));
        } catch (Exception e) {
            log.error("通过account批量查询平台用户异常, account = {}", accounts, e);
            return;
        }
        Map<String, EmployeeBaseInfo> employeeBaseInfoMap = employeeBaseInfoList.stream().collect(Collectors.toMap(EmployeeBaseInfo::getAccount, Function.identity()));
        if (!CollectionUtils.isEmpty(employeeBaseInfoMap)) {
            for (String account : accounts) {
                EmployeeBaseInfo employeeBaseInfo = employeeBaseInfoMap.get(account);
                if (Objects.nonNull(employeeBaseInfo) && StringUtils.hasText(employeeBaseInfo.getId())) {
                    ACCOUNT_EMPLOYEE_INFO_CACHE.put(account, Optional.ofNullable(employeeBaseInfo.getId()));
                } else {
                    log.info("通过account批量查询平台用户，未查询到EmployeeUserInfo对象，或者EmployeeUserInfo对象未设置employeeId,  account = {}", account);
                    ACCOUNT_EMPLOYEE_INFO_CACHE.put(account, Optional.empty());
                }
            }
        }
    }

    private static Optional<String> addUserIdMapCache(String userId) {
        if (!StringUtils.hasText(userId)) {
            return Optional.empty();
        }
        EmployeeListUserIdParam param = new EmployeeListUserIdParam();
        param.setUserIdList(Collections.singletonList(userId));
        EmployeeUserInfo employeeUserInfo = null;
        try {
            List<EmployeeUserInfo> employeeUserInfoList = EmployeeService.listByUserId(param);
            employeeUserInfo = employeeUserInfoList.get(0);
        } catch (Exception e) {
            log.error("通过userId批量查询平台用户异常, userId = {}", userId, e);
            return Optional.empty();
        }
        if (Objects.nonNull(employeeUserInfo)) {
            String employeeId = employeeUserInfo.getEmployeeId();
            return Optional.ofNullable(employeeId);
        } else {
            return Optional.empty();
        }
    }


    private static void addUserIdMapCacheBatch(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        EmployeeListUserIdParam param = new EmployeeListUserIdParam();
        param.setUserIdList(userIds);
        List<EmployeeUserInfo> employeeUserInfoList;
        try {
            employeeUserInfoList = EmployeeService.listByUserId(param);
        } catch (Exception e) {
            log.error("通过userId批量查询平台用户异常, userId = {}", param.getUserIdList(), e);
            return;
        }
        Map<String, EmployeeUserInfo> employeeUserInfoMap = employeeUserInfoList.stream().collect(Collectors.toMap(EmployeeUserInfo::getUserId, Function.identity()));
        if (!CollectionUtils.isEmpty(employeeUserInfoMap)) {
            for (String userId : userIds) {
                EmployeeUserInfo employeeUserInfo = employeeUserInfoMap.get(userId);
                if (Objects.nonNull(employeeUserInfo) && StringUtils.hasText(employeeUserInfo.getEmployeeId())) {
                    USER_ID_MAP_EMPLOYEE_ID_CACHE.put(userId, Optional.ofNullable(employeeUserInfo.getEmployeeId()));
                } else {
                    log.info("通过userId批量查询平台用户，未查询到EmployeeUserInfo对象，或者EmployeeUserInfo对象未设置employeeId,  userId = {}", userId);
                    USER_ID_MAP_EMPLOYEE_ID_CACHE.put(userId, Optional.empty());
                }
            }
        }
    }

}
