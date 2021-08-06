package com.infotecs.intern.model.repository;

import com.infotecs.intern.model.Pair;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface PairRepository extends CrudRepository<Pair, Long> {

    @Override
    Optional<Pair> findById(Long aLong);

    Optional<Pair> findValueByKey(String key);

    void deleteByKey(String key);

    @Modifying
    @Transactional
    @Query("DELETE FROM pair\n" +
            "WHERE \n" +
            "PARSEDATETIME(timeStamp,'yyyyMMddHHmmss') <= CURRENT_TIMESTAMP()\n")
    void cleanExpiredTTL();

}
