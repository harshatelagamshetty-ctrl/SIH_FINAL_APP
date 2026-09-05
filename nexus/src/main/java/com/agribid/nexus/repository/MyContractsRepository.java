package com.agribid.nexus.repository;

import com.agribid.nexus.domain.contract.ForwardContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Deliberately a SEPARATE repository interface from your existing
 * ForwardContractRepository, even though both target the same
 * entity — Spring Data JPA fully supports this. This is intentional:
 * it lets these new queries be added without touching (and risking
 * clobbering something in) your existing repository file at all.
 */
public interface MyContractsRepository extends JpaRepository<ForwardContract, Long> {

    @Query("SELECT c FROM ForwardContract c JOIN FETCH c.sourceListing l JOIN FETCH l.cropLot cl JOIN FETCH cl.category WHERE cl.owner.id = :farmerId ORDER BY c.deliveryDeadline DESC")
    List<ForwardContract> findByFarmerId(Long farmerId);

    /**
     * Identifies "the winning distributor" the same way
     * BidListingServiceImpl.convertToContract does — the bid with
     * the maximum amount on the listing behind this contract.
     */
    @Query("""
        SELECT c FROM ForwardContract c
        JOIN FETCH c.sourceListing l
        JOIN FETCH l.cropLot cl
        JOIN FETCH cl.category
        JOIN l.bids b
        WHERE b.bidder.id = :distributorId
        AND b.amount = (SELECT MAX(b2.amount) FROM Bid b2 WHERE b2.listing = l)
        ORDER BY c.deliveryDeadline DESC
        """)
    List<ForwardContract> findByWinningDistributorId(Long distributorId);
}