package io.netpulse.cloud.infrastructure.persistence.repository;

import io.netpulse.cloud.domain.model.DeviceStatus;
import io.netpulse.cloud.infrastructure.persistence.entity.DeviceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataDeviceRepository
        extends JpaRepository<DeviceJpaEntity, UUID>, JpaSpecificationExecutor<DeviceJpaEntity> {

    boolean existsByHostname(String hostname);

    boolean existsByManagementIp(String managementIp);

    long countByStatus(DeviceStatus status);
}
