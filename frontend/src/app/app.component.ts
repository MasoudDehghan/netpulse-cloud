import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DeviceFleetStore } from './core/services/device-fleet.store';
import { DeviceStatus } from './core/models/device.model';

@Component({
  selector: 'np-root',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="shell">
      <header class="topbar">
        <div>
          <h1>NetPulse Cloud</h1>
          <p class="subtitle">Multi-Vendor Network Inventory &amp; Real-Time Telemetry Console</p>
        </div>
        <div class="availability-pill">
          Fleet Availability: <strong>{{ store.availabilityPct() }}%</strong>
        </div>
      </header>

      <section class="kpi-grid">
        <div class="kpi-card">
          <span class="label">Total Fleet</span>
          <span class="value">{{ store.summary().totalDevices }}</span>
        </div>
        <div class="kpi-card healthy">
          <span class="label">Healthy</span>
          <span class="value">{{ store.summary().healthyCount }}</span>
        </div>
        <div class="kpi-card degraded">
          <span class="label">Degraded</span>
          <span class="value">{{ store.summary().degradedCount }}</span>
        </div>
        <div class="kpi-card unreachable">
          <span class="label">Unreachable</span>
          <span class="value">{{ store.summary().unreachableCount }}</span>
        </div>
      </section>

      <section class="toolbar">
        @for (filter of statusFilters; track filter) {
          <button
            type="button"
            [class.active]="store.selectedStatus() === filter"
            (click)="store.loadFleet(filter)">
            {{ filter }}
          </button>
        }
      </section>

      @if (store.error(); as err) {
        <div class="banner-warn">{{ err }}</div>
      }

      <section class="table-card">
        <table>
          <thead>
            <tr>
              <th>Hostname</th>
              <th>Management IP</th>
              <th>Vendor</th>
              <th>Region</th>
              <th>Firmware</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            @for (device of store.devices(); track device.id) {
              <tr>
                <td class="mono">{{ device.hostname }}</td>
                <td class="mono">{{ device.managementIp }}</td>
                <td>{{ device.vendor }}</td>
                <td>{{ device.region }}</td>
                <td>{{ device.firmwareVersion }}</td>
                <td>
                  <span class="badge" [attr.data-status]="device.status">{{ device.status }}</span>
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="6" class="empty">No network devices registered in current filter.</td>
              </tr>
            }
          </tbody>
        </table>
      </section>
    </div>
  `,
  styles: [`
    .shell { max-width: 1180px; margin: 0 auto; padding: 28px 20px; }
    .topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    h1 { margin: 0; font-size: 1.55rem; letter-spacing: -0.02em; }
    .subtitle { margin: 4px 0 0; color: var(--np-muted); font-size: 0.9rem; }
    .availability-pill { background: var(--np-surface); border: 1px solid var(--np-border); padding: 8px 14px; border-radius: 8px; font-size: 0.9rem; }
    .kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 20px; }
    .kpi-card { background: var(--np-surface); border: 1px solid var(--np-border); border-radius: 10px; padding: 16px; display: flex; flex-direction: column; gap: 6px; }
    .kpi-card .label { color: var(--np-muted); font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.05em; }
    .kpi-card .value { font-size: 1.7rem; font-weight: 700; }
    .kpi-card.healthy .value { color: var(--np-healthy); }
    .kpi-card.degraded .value { color: var(--np-degraded); }
    .kpi-card.unreachable .value { color: var(--np-unreachable); }
    .toolbar { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }
    .toolbar button { background: var(--np-surface); color: var(--np-muted); border: 1px solid var(--np-border); border-radius: 6px; padding: 6px 12px; cursor: pointer; font-size: 0.82rem; }
    .toolbar button.active { border-color: var(--np-accent); color: var(--np-accent); }
    .banner-warn { background: rgba(245, 158, 11, 0.12); border: 1px solid var(--np-degraded); color: #fcd34d; padding: 10px 14px; border-radius: 8px; margin-bottom: 16px; font-size: 0.88rem; }
    .table-card { background: var(--np-surface); border: 1px solid var(--np-border); border-radius: 10px; overflow: hidden; }
    table { width: 100%; border-collapse: collapse; text-align: left; font-size: 0.9rem; }
    th, td { padding: 12px 16px; border-bottom: 1px solid var(--np-border); }
    th { color: var(--np-muted); font-weight: 600; font-size: 0.78rem; text-transform: uppercase; letter-spacing: 0.04em; }
    .mono { font-family: ui-monospace, SFMono-Regular, Menlo, monospace; font-size: 0.85rem; }
    .badge { padding: 3px 8px; border-radius: 999px; font-size: 0.75rem; font-weight: 600; background: #1f2937; }
    .empty { text-align: center; color: var(--np-muted); padding: 28px; }
  `]
})
export class AppComponent implements OnInit {
  readonly store = inject(DeviceFleetStore);
  readonly statusFilters: Array<DeviceStatus | 'ALL'> = [
    'ALL',
    'HEALTHY',
    'DEGRADED',
    'UNREACHABLE',
    'PROVISIONING',
    'MAINTENANCE'
  ];

  ngOnInit(): void {
    this.store.loadFleet('ALL');
  }
}
