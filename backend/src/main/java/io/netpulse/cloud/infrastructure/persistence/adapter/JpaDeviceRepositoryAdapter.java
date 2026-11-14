package io.netpulse.cloud.infrastructure.persistence.adapter;

import io.netpulse.cloud.domain.model.Device;
import io.netpulse.cloud.domain.model.DeviceFilter;
import io.netpulse.cloud.domain.model.DeviceStatus;
import io.netpulse.cloud.domain.model.FleetSummary;
import io.netpulse.cloud.domain.model.PageResult;
import io.netpulse.cloud.domain.port.DeviceRepositoryPort;
import io.netpulse.cloud.infrastructure.persistence.entity.DeviceJpaEntity;
import io.netpulse.cloud.infrastructure.persistence.repository.SpringDataDeviceRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDeviceRepositoryAdapter implements DeviceRepositoryPort {

    private final SpringDataDeviceRepository jpaRepository;

    public JpaDeviceRepositoryAdapter(SpringDataDeviceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByHostname(String hostname) {
        return jpaRepository.existsByHostname(hostname);
    }

    @Override
    public boolean existsByManagementIp(String managementIp) {
        return jpaRepository.existsByManagementIp(managementIp);
    }

    @Override
    public Device save(Device device) {
        DeviceJpaEntity saved = jpaRepository.save(toEntity(device));
        return toDomain(saved);
    }

    @Override
    public Optional<Device> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public PageResult<Device> findByFilter(DeviceFilter filter) {
        Specification<DeviceJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.vendor() != null) {
                predicates.add(cb.equal(root.get("vendor"), filter.vendor()));
            }
            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }
            if (filter.region() != null && !filter.region().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("region")), filter.region().trim().toLowerCase()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        PageRequest pageable = PageRequest.of(
                filter.page(),
                filter.size(),
                Sort.by(Sort.Direction.ASC, "hostname")
        );
        Page<DeviceJpaEntity> page = jpaRepository.findAll(spec, pageable);

        return new PageResult<>(
                page.getContent().stream().map(this::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public FleetSummary computeFleetSummary() {
        long total = jpaRepository.count();
        long healthy = jpaRepository.countByStatus(DeviceStatus.HEALTHY);
        long degraded = jpaRepository.countByStatus(DeviceStatus.DEGRADED);
        long unreachable = jpaRepository.countByStatus(DeviceStatus.UNREACHABLE);
        long maintenance = jpaRepository.countByStatus(DeviceStatus.MAINTENANCE);
        return new FleetSummary(total, healthy, degraded, unreachable, maintenance);
    }

    private DeviceJpaEntity toEntity(Device d) {
        return new DeviceJpaEntity(
                d.id(),
                d.hostname(),
                d.managementIp(),
                d.vendor(),
                d.region(),
                d.firmwareVersion(),
                d.status(),
                d.lastSeenAt(),
                d.createdAt(),
                d.updatedAt(),
                d.version()
        );
    }

    private Device toDomain(DeviceJpaEntity e) {
        return new Device(
                e.getId(),
                e.getHostname(),
                e.getManagementIp(),
                e.getVendor(),
                e.getRegion(),
                e.getFirmwareVersion(),
                e.getStatus(),
                e.getLastSeenAt(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getVersion()
        );
    }
}
