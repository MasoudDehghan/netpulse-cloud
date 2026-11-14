export type DeviceVendor =
  | 'CISCO_IOS_XE'
  | 'CISCO_NX_OS'
  | 'JUNIPER_JUNOS'
  | 'IDIRECT_SAT'
  | 'HNS_SAT';

export type DeviceStatus =
  | 'PROVISIONING'
  | 'HEALTHY'
  | 'DEGRADED'
  | 'UNREACHABLE'
  | 'MAINTENANCE';

export interface Device {
  id: string;
  hostname: string;
  managementIp: string;
  vendor: DeviceVendor;
  region: string;
  firmwareVersion: string;
  status: DeviceStatus;
  lastSeenAt?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface DevicePage {
  items: Device[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface FleetSummary {
  totalDevices: number;
  healthyCount: number;
  degradedCount: number;
  unreachableCount: number;
  maintenanceCount: number;
}
