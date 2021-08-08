package com.infotecs.intern.model.service;

import com.infotecs.intern.model.Pair;
import com.infotecs.intern.model.repository.PairRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
public class PairService {

    private static final String DUMP_NAME = "dump.csv";
    private static final String TEMP_NAME = "tempDump.csv";
    private final PairRepository pairRepository;
    private final DateTimeFormatter dateTimeFormatter;

    public PairService(PairRepository pairRepository) {
        this.pairRepository = pairRepository;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
    }

    public boolean isTimeStampExpired(String timeStamp) {
        return LocalDateTime
                .parse(timeStamp, dateTimeFormatter)
                .isBefore(LocalDateTime.now());
    }

    public void copyFile(MultipartFile file, Path path) throws IOException {
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(file.getBytes());
        }
    }

    public void load(MultipartFile file) {
        log.info("Trying to load from {}", file.getOriginalFilename());
        Path path = Paths.get(TEMP_NAME);

        try {
            copyFile(file, path);
            pairRepository.dropPair();
            pairRepository.load(TEMP_NAME);
            Files.delete(path);
            log.info("Loaded successfully");
        } catch (IOException e) {
            log.error("Couldn't make a temp. {}", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Error in H2. {}", e.getMessage());
        }

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

    public Iterable<Pair> getPairs() {
        log.info("Returning a list of the pairs");
        List<Pair> actualPairs = new ArrayList<>();

        for (Pair pair : pairRepository.findAll()) {
            if (!isTimeStampExpired(pair.getTimeStamp()))
                actualPairs.add(pair);
        }

        return actualPairs;
    }

    public Resource dump() {
        log.info("Dumping data");
        try {
            Path file = Paths
                    .get("")
                    .resolve(Paths
                            .get(DUMP_NAME).normalize()).toAbsolutePath();

            log.info("Request to dump to {}", file);
            cleanExpiredTTL();
            pairRepository.dump(file.toString());
            log.info("The query is done successfully");

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                log.info("Dumped successfully");
                return resource;
            } else {
                throw new MalformedURLException("Error: wrong path " + file);
            }
        } catch (MalformedURLException e) {
            log.error("Error while getting dump. {}", e.getMessage());
            return null;
        } catch (DataAccessException e) {
            log.error("Error in H2. {}", e.getMessage());
            return null;
        }
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

    @Transactional
    public String deleteValueByKey(String key) {
        String value = getValueByKey(key);
        pairRepository.deleteByKey(key);
        log.info("Delete {}:{}", key, value);
        return value;
    }

    @Scheduled(fixedDelay = 100000)
    public void cleanExpiredTTL() {
        log.info("Scheduled job: clean expired TTL");
        pairRepository.cleanExpiredTTL();
    }

    public boolean savePair(String key, String value, Integer ttl) {
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
        return true;
    }

}
