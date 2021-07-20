package com.megait.myhome.repository;

import com.megait.myhome.domain.Member;
import com.megait.myhome.domain.Order;
import com.megait.myhome.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Order 중에, member, status (Cart)

    // SELECT * FROM orders WHERE status = ? AND member = ?
    Optional<Order> findByStatusAndMember(Status status, Member member);
}
