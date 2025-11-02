package org.example.sciencesocialmedia.repository;

import org.example.sciencesocialmedia.entity.Subscription;
import org.example.sciencesocialmedia.entity.id.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    @Query("select count(s) from subscriptions s where s.authorId = :userId")
    int countSubscribersByAuthorId(String userId);

    @Query("select count(s) from subscriptions s where s.subscriberId = :subscriberId")
    int countSubscriptionsBySubscriberId(String subscriberId);

    boolean existsByAuthorIdAndSubscriberId(String authorId, String subscriberId);

    @Query("select s.subscriberId from subscriptions s where s.authorId = :authorId")
    List<String> findSubscriberIdsByAuthorId(String authorId);

    @Query("select s.authorId from subscriptions s where s.subscriberId = :subscriberId")
    List<String> findSubscriptionIdsBySubscriberId(String subscriberId);
}
