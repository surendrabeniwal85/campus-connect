import PortalLoginForm from '../auth/PortalLoginForm';
import type { AuthenticatedSession } from '../auth/authApi';

interface PlacementLoginProps {
  readonly onAuthenticated: (session: AuthenticatedSession) => void;
  readonly onChangePortal: () => void;
  readonly onResetPassword: () => void;
}

export default function PlacementLogin(props: PlacementLoginProps) {
  return (
    <PortalLoginForm
      {...props}
      portal="PLACEMENT"
      title="Placement Portal sign in"
      description="Access for Director HR (master), TPO, D.TPO, and Placement Coordinators."
    />
  );
}
