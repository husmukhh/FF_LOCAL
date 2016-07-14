package com.ff.dao;

public interface SQLUpdateQuries {
	String USER_UPDATE_ACTIVE = "UPDATE users u, user_session us SET u.active = ? WHERE u.id = us.user_id AND u.username = ? AND u.email = ? AND us.session_token = ?";
}
