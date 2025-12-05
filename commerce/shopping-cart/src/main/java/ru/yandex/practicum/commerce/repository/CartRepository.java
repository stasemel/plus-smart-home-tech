package ru.yandex.practicum.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.commerce.model.ShoppingCart;

import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, UUID> {
    ShoppingCart findByUserName(String userName);
}
