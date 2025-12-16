package ru.yandex.practicum.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.commerce.model.ShoppingCart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, UUID> {
    ShoppingCart findByUserName(String userName);

    List<ShoppingCart> findAllByUserName(String userName);

    List<ShoppingCart> findAllByUserNameAndActivity(String userName, boolean b);

    Optional<ShoppingCart> findByUserNameAndActivity(String userName, boolean b);
}
