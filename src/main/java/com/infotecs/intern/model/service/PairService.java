package com.infotecs.intern.model.service;

import com.infotecs.intern.model.Pair;
import com.infotecs.intern.model.repository.PairRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

@Service
@Slf4j
@EnableScheduling
public class PairService {

    private static final String DUMP_NAME = "dump.csv";
    private static final String TEMP_NAME = "tempDump.csv";
    private final PairRepository pairRepository;

    @Autowired
    public PairService(PairRepository pairRepository) {
        this.pairRepository = pairRepository;
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
            pairRepository.dropTablePair();
            pairRepository.load(TEMP_NAME);
            Files.delete(path);
            log.info("Loaded successfully");
        } catch (IOException | DataAccessException e) {
            e.printStackTrace();
        }

    }

    public Optional<Pair> getPairByKey(String key) {
        log.info("Get pair by key: {}", key);
        Optional<Pair> pair = pairRepository.findValueByKey(key);
        log.info(pair.isPresent() ? pair.get().toString() : "The pair is null");
        return pair;
    }

    public String getValueByKey(String key) {
        log.info("Returning value by key = {}", key);
        Optional<Pair> pair = getPairByKey(key);
        return pair.isPresent() ? pair.get().getValue() : "null";
    }

    public Iterable<Pair> getPairs() {
        Iterable<Pair> pairs = pairRepository.findAll();
        log.info("Returning a list of the pairs: {}", pairs);
        return pairs;
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
        } catch (MalformedURLException | DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public String deletePairByKey(String key) {
        String value = getValueByKey(key);
        pairRepository.deletePairByKey(key);
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

        if (pair.isPresent()) {
            pair.get().setTimeStamp(ttl);
            pair.get().setValue(value);
        } else {
            pairRepository.save(new Pair(key, value, ttl));
        }

        log.info("Saved successfully");
        return true;
    }

}
