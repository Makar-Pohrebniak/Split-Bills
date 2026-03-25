package com.example.SplitBills.service.api;

import com.example.SplitBills.model.dto.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendService {

    void addFriend(String friendId);

    List<UserResponse> getFriends();

    void removeFriend(String friendId);
}
