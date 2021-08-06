package com.infotecs.intern.model.service;

import com.infotecs.intern.model.Pair;
import com.infotecs.intern.model.repository.PairRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
public class PairService {

    private final PairRepository pairRepository;
    private final DateTimeFormatter dateTimeFormatter;

    public PairService(PairRepository pairRepository) {
        this.pairRepository = pairRepository;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    }

    public boolean isTimeStampExpired(String timeStamp) {
        return LocalDateTime
                .parse(timeStamp, dateTimeFormatter)
                .isBefore(LocalDateTime.now());
    }

    public String getValueByKey(String key) {
        log.info("Returning value by key = {}", key);
        Optional<Pair> pair = getPairByKey(key);

        if (pair.isPresent() && isTimeStampExpired(pair.get().getTimeStamp())) {
            log.info("TTL expired, return null");
            return "null";
        }

        return pair.isPresent() ? pair.get().getValue() : "null";
    }

    public Optional<Pair> getPairByKey(String key) {
        log.info("Get pair by key: {}", key);
        Optional<Pair> pair = pairRepository.findValueByKey(key);

        if (pair.isPresent() && isTimeStampExpired(pair.get().getTimeStamp())) {
            log.info("TTL expired, return null");
            return Optional.empty();
        }

        log.info(pair.isPresent() ? pair.get().toString() : "The pair is null");
        return pair;
    }

    public String deleteValueByKey(String key) {
        String value = getValueByKey(key);
        pairRepository.deleteByKey(key);
        log.info("Delete {}:{}", key, value);
        return value;
    }

    @Scheduled(fixedDelay = 10000)
    public void cleanExpiredTTL() {
        log.info("Scheduled job: clean expired TTL");
        pairRepository.cleanExpiredTTL();
    }

    public void savePair(String key, String value, Integer ttl) {
        log.info("Saving {}:{} to the repository with ttl: {}", key, value, ttl);
        Optional<Pair> pair = getPairByKey(key);
        String time = LocalDateTime.now().plusSeconds(ttl).format(dateTimeFormatter);
        log.info("Time to delete = {}", time);

        if (pair.isPresent()) {
            pair.get().setTimeStamp(time);
            pair.get().setValue(value);
        } else {
            pairRepository.save(new Pair(key, value, time));
        }

        log.info("Saved successfully");
    }

}
