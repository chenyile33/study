package com.example.study.user.service;

import com.example.common.core.error.CommonErrorCode;
import com.example.common.core.exception.BusinessException;
import com.example.common.core.page.PageParam;
import com.example.common.core.page.PageResult;
import com.example.common.core.util.AssertUtils;
import com.example.study.user.domain.UserProfile;
import com.example.study.user.dto.CreateUserRequest;
import com.example.study.user.dto.UserDetailResponse;
import com.example.study.user.dto.UserListItemResponse;
import com.example.study.user.repository.InMemoryUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDemoService {

    private final InMemoryUserRepository userRepository;

    public UserDemoService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetailResponse createUser(CreateUserRequest request) {
        validateCreateRequest(request);

        String username = request.getUsername().trim();
        AssertUtils.isTrue(
                !userRepository.existsByUsername(username),
                CommonErrorCode.PARAM_ERROR,
                "username已存在"
        );

        UserProfile userProfile = userRepository.save(
                username,
                request.getNickname().trim(),
                request.getEmail().trim()
        );
        return UserDetailResponse.from(userProfile);
    }

    public UserDetailResponse getUser(Long id) {
        AssertUtils.notNull(id, CommonErrorCode.PARAM_ERROR, "id不能为空");
        AssertUtils.isTrue(id > 0, CommonErrorCode.PARAM_ERROR, "id必须大于0");

        UserProfile userProfile = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.PARAM_ERROR, "用户不存在"));
        return UserDetailResponse.from(userProfile);
    }

    public PageResult<UserListItemResponse> pageUsers(PageParam pageParam, String keyword) {
        PageParam normalizedPageParam = pageParam == null ? new PageParam() : pageParam;
        List<UserProfile> matchedUsers = userRepository.search(keyword);
        List<UserListItemResponse> records = pageSlice(matchedUsers, normalizedPageParam).stream()
                .map(UserListItemResponse::from)
                .toList();

        return PageResult.of(records, matchedUsers.size(), normalizedPageParam);
    }

    private static void validateCreateRequest(CreateUserRequest request) {
        AssertUtils.notNull(request, CommonErrorCode.PARAM_ERROR, "请求体不能为空");
        AssertUtils.hasText(request.getUsername(), CommonErrorCode.PARAM_ERROR, "username不能为空");
        AssertUtils.hasText(request.getNickname(), CommonErrorCode.PARAM_ERROR, "nickname不能为空");
        AssertUtils.hasText(request.getEmail(), CommonErrorCode.PARAM_ERROR, "email不能为空");
        AssertUtils.isTrue(request.getEmail().contains("@"), CommonErrorCode.PARAM_ERROR, "email格式错误");
    }

    private static List<UserProfile> pageSlice(List<UserProfile> records, PageParam pageParam) {
        long offset = pageParam.offset();
        if (offset >= records.size()) {
            return List.of();
        }

        int fromIndex = (int) offset;
        int toIndex = Math.min(fromIndex + pageParam.limit(), records.size());
        return records.subList(fromIndex, toIndex);
    }
}
