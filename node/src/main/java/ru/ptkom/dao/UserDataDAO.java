package ru.ptkom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ptkom.entity.UserData;

public interface UserDataDAO extends JpaRepository<UserData, Long> {
}
