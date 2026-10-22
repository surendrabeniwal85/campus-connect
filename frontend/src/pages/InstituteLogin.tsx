import PortalLoginForm from '../auth/PortalLoginForm';
import type { AuthenticatedSession } from '../auth/authApi';

interface InstituteLoginProps {
  readonly onAuthenticated: (session: AuthenticatedSession) => void;
  readonly onChangePortal: () => void;
  readonly onResetPassword: () => void;
  readonly onRegister?: () => void;
}

export default function InstituteLogin(props: InstituteLoginProps) {
  return (
    <PortalLoginForm
      {...props}
      portal="INSTITUTE"
      title="Institute Portal sign in"
      description="Access for Students, Teachers, and the HOD portal master."
    />
  );
}
