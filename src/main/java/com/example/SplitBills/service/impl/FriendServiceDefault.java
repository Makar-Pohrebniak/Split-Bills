package com.example.SplitBills.service.impl;

import com.example.SplitBills.exception.YouAreNotYourFriendException;
import com.example.SplitBills.exception.UserNotFoundException;
import com.example.SplitBills.model.dto.response.UserResponse;
import com.example.SplitBills.model.entity.UserEntity;
import com.example.SplitBills.repository.UserRepository;
import com.example.SplitBills.service.api.FriendService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceDefault implements FriendService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addFriend(String friendId) {
        UserEntity me = getCurrentUser();


        UserEntity friend = userRepository.findBySubId(friendId)
                .orElseThrow(() -> new UserNotFoundException(friendId));

        if(me.getSubId().equals(friend.getSubId())) {
            throw new YouAreNotYourFriendException();
        }

        me.getFriends().add(friend);
    }

    @Override
    @Transactional
    public void removeFriend(String friendId) {
        UserEntity me = getCurrentUser();

        UserEntity friend = userRepository.findBySubId(friendId)
                .orElseThrow(() -> new UserNotFoundException(friendId));

        if(me.getSubId().equals(friend.getSubId())) {
            throw new YouAreNotYourFriendException();
        }

        me.getFriends().remove(friend);
    }

    @Override
    @Transactional
    public List<UserResponse> getFriends(){
        UserEntity me = getCurrentUser();

        return me.getFriends().stream()
                .map(friends->UserResponse.builder()
                        .subId(String.valueOf(friends.getSubId()))
                        .username(friends.getUsername())
                        .email(friends.getEmail())
                        .build())
                .toList();
    }

    private UserEntity getCurrentUser() {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findBySubId(identifier)
                .orElseThrow(() -> new UserNotFoundException(identifier));
    }
}
