package com.infotecs.intern.model.repository;

import com.infotecs.intern.model.Pair;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
            "PARSEDATETIME(timeStamp,'yyyy-MM-dd.HH:mm:ss') <= CURRENT_TIMESTAMP()\n")
    void cleanExpiredTTL();

    @Transactional
    @Query(value = "CALL CSVWRITE(:path, 'SELECT * FROM pair')", nativeQuery = true)
    void dump(@Param("path") String path);

    @Transactional
    @Modifying
    @Query(value = "CREATE TABLE pair AS SELECT * FROM CSVREAD('tempDump.csv')", nativeQuery = true)
    void load(@Param("fileName") String path);

    @Modifying
    @Transactional
    @Query(value = "DROP TABLE pair", nativeQuery = true)
    void dropPair();
}
