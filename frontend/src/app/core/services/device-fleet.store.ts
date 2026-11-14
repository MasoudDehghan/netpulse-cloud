import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Device, DevicePage, DeviceStatus, FleetSummary } from '../models/device.model';

@Injectable({ providedIn: 'root' })
export class DeviceFleetStore {
  private readonly http = inject(HttpClient);
  private readonly apiBase = 'http://localhost:8080/api/v1/devices';

  readonly devices = signal<Device[]>([]);
  readonly summary = signal<FleetSummary>({
    totalDevices: 0,
    healthyCount: 0,
    degradedCount: 0,
    unreachableCount: 0,
    maintenanceCount: 0
  });
  readonly selectedStatus = signal<DeviceStatus | 'ALL'>('ALL');
  readonly loading = signal<boolean>(false);
  readonly error = signal<string | null>(null);

  readonly availabilityPct = computed(() => {
    const s = this.summary();
    if (s.totalDevices === 0) {
      return 100;
    }
    return Math.round((s.healthyCount / s.totalDevices) * 1000) / 10;
  });

  loadFleet(statusFilter: DeviceStatus | 'ALL' = this.selectedStatus()): void {
    this.selectedStatus.set(statusFilter);
    this.loading.set(true);
    this.error.set(null);

    let params = new HttpParams().set('page', 0).set('size', 25);
    if (statusFilter !== 'ALL') {
      params = params.set('status', statusFilter);
    }

    this.http.get<DevicePage>(this.apiBase, { params }).subscribe({
      next: (page) => {
        this.devices.set(page.items);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Backend API unreachable at localhost:8080 — start Spring Boot & Docker Compose.');
        this.loading.set(false);
      }
    });

    this.http.get<FleetSummary>(`${this.apiBase}/fleet-summary`).subscribe({
      next: (kpi) => this.summary.set(kpi),
      error: () => {}
    });
  }
}
