package com.mburakaltun.couriertracking.common.config;

import com.mburakaltun.couriertracking.model.entity.CourierEntity;
import com.mburakaltun.couriertracking.model.entity.StoreEntity;
import com.mburakaltun.couriertracking.repository.CourierJpaRepository;
import com.mburakaltun.couriertracking.repository.StoreJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class DataInitializerConfig {

    @Bean
    public CommandLineRunner initializeStoreData(StoreJpaRepository storeJpaRepository, CourierJpaRepository courierJpaRepository) {
        return args -> {
            if (storeJpaRepository.count() == 0) {
                List<StoreEntity> stores = Arrays.asList(
                        createStore("Ataşehir MMM Migros", 40.9923307, 29.1244229),
                        createStore("Novada MMM Migros", 40.986106, 29.1161293),
                        createStore("Beylikdüzü 5M Migros", 41.0066851, 28.6552262),
                        createStore("Ortaköy MMM Migros", 41.055783, 29.0210292),
                        createStore("Caddebostan MMM Migros", 40.9632463, 29.0630908)
                );

                storeJpaRepository.saveAll(stores);

                log.info("Store data initialized successfully!");
            }

            if (courierJpaRepository.count() == 0) {
                CourierEntity courierEntity = new CourierEntity();
                courierEntity.setName("Default Courier");
                courierEntity.setTotalDistance(0.0);
                courierJpaRepository.save(courierEntity);

                log.info("Courier data initialized successfully!");
            }
        };
    }

    private StoreEntity createStore(String name, Double latitude, Double longitude) {
        StoreEntity store = new StoreEntity();
        store.setName(name);
        store.setLatitude(latitude);
        store.setLongitude(longitude);
        return store;
    }
}