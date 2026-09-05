package com.agribid.nexus.repository;

import com.agribid.nexus.domain.contract.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * A separate, additive-only repository interface for Order, same
 * reasoning as MyContractsRepository — never touches your existing
 * OrderRepository file.
 */
public interface MyOrdersRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.contract c JOIN FETCH c.sourceListing l JOIN FETCH l.cropLot cl JOIN FETCH cl.category WHERE cl.owner.id = :farmerId ORDER BY o.createdAt DESC")
    List<Order> findByFarmerId(Long farmerId);

    @Query("""
        SELECT o FROM Order o
        JOIN FETCH o.contract c
        JOIN FETCH c.sourceListing l
        JOIN FETCH l.cropLot cl
        JOIN FETCH cl.category
        JOIN l.bids b
        WHERE b.bidder.id = :distributorId
        AND b.amount = (SELECT MAX(b2.amount) FROM Bid b2 WHERE b2.listing = l)
        ORDER BY o.createdAt DESC
        """)
    List<Order> findByWinningDistributorId(Long distributorId);
}