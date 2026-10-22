export type Portal = 'INSTITUTE' | 'PLACEMENT';

export type ModuleOwner =
  | 'Surendra Beniwal'
  | 'Naman Relia'
  | 'Pallavi Sharma'
  | 'Aliza Imran'
  | 'Ashish Sharma';

export interface ModuleRoute {
  readonly path: string;
  readonly title: string;
  readonly view?:
    | 'PORTAL_SELECTION'
    | 'INSTITUTE_LOGIN'
    | 'PLACEMENT_LOGIN'
    | 'INSTITUTE_REGISTER'
    | 'INSTITUTE_RESET_PASSWORD'
    | 'PLACEMENT_RESET_PASSWORD'
    | 'PORTAL_DASHBOARD'
    | 'AUTH_SESSION';
}

export interface ModuleRegistration {
  readonly id: string;
  readonly portal: Portal | 'SHARED';
  readonly owner: ModuleOwner;
  readonly folder: string;
  readonly routes: readonly ModuleRoute[];
}

export const moduleRegistry = [
  {
    id: 'institute-records',
    portal: 'INSTITUTE',
    owner: 'Surendra Beniwal',
    folder: 'features/institute/records',
    routes: [{ path: '/institute/records', title: 'Student Records' }],
  },
  {
    id: 'institute-classroom',
    portal: 'INSTITUTE',
    owner: 'Naman Relia',
    folder: 'features/institute/classroom',
    routes: [{ path: '/institute/classroom', title: 'Classroom and Training' }],
  },
  {
    id: 'institute-hub',
    portal: 'INSTITUTE',
    owner: 'Pallavi Sharma',
    folder: 'features/institute/hub',
    routes: [{ path: '/institute/hub', title: 'Student Hub' }],
  },
  {
    id: 'placement-drives',
    portal: 'PLACEMENT',
    owner: 'Aliza Imran',
    folder: 'features/placement/drives',
    routes: [{ path: '/placement/drives', title: 'Drives and Opportunities' }],
  },
  {
    id: 'placement-selection',
    portal: 'PLACEMENT',
    owner: 'Ashish Sharma',
    folder: 'features/placement/selection',
    routes: [{ path: '/placement/selection', title: 'Selection and Communication' }],
  },
  {
    id: 'public-portal-entry',
    portal: 'SHARED',
    owner: 'Surendra Beniwal',
    folder: 'auth/PortalSelection.tsx',
    routes: [{ path: '/', title: 'Portal selection', view: 'PORTAL_SELECTION' }],
  },
  {
    id: 'institute-login',
    portal: 'INSTITUTE',
    owner: 'Surendra Beniwal',
    folder: 'pages/InstituteLogin.tsx',
    routes: [{ path: '/institute/login', title: 'Institute Login', view: 'INSTITUTE_LOGIN' }],
  },
  {
    id: 'placement-login',
    portal: 'PLACEMENT',
    owner: 'Surendra Beniwal',
    folder: 'pages/PlacementLogin.tsx',
    routes: [{ path: '/placement/login', title: 'Placement Login', view: 'PLACEMENT_LOGIN' }],
  },
  {
    id: 'auth-session',
    portal: 'SHARED',
    owner: 'Surendra Beniwal',
    folder: 'auth/AuthApplication.tsx',
    routes: [{ path: '/auth/session', title: 'Authentication Session', view: 'AUTH_SESSION' }],
  },
  {
    id: 'institute-reset-password',
    portal: 'INSTITUTE',
    owner: 'Surendra Beniwal',
    folder: 'pages/ResetPassword.tsx',
    routes: [{
      path: '/institute/reset-password',
      title: 'Institute Password Reset',
      view: 'INSTITUTE_RESET_PASSWORD',
    }],
  },
  {
    id: 'placement-reset-password',
    portal: 'PLACEMENT',
    owner: 'Surendra Beniwal',
    folder: 'pages/ResetPassword.tsx',
    routes: [{
      path: '/placement/reset-password',
      title: 'Placement Password Reset',
      view: 'PLACEMENT_RESET_PASSWORD',
    }],
  },
  {
    id: 'institute-register',
    portal: 'INSTITUTE',
    owner: 'Surendra Beniwal',
    folder: 'pages/RegisterStudent.tsx',
    routes: [{
      path: '/institute/register',
      title: 'Student Account Claim',
      view: 'INSTITUTE_REGISTER',
    }],
  },
  {
    id: 'portal-dashboard',
    portal: 'SHARED',
    owner: 'Surendra Beniwal',
    folder: 'auth/PortalDashboard.tsx',
    routes: [
      { path: '/dashboard', title: 'Portal Dashboard', view: 'PORTAL_DASHBOARD' },
      { path: '/portal/dashboard', title: 'Portal Dashboard', view: 'PORTAL_DASHBOARD' },
    ],
  },
] as const satisfies readonly ModuleRegistration[];
