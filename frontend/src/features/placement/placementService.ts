const API_BASE = '/api/v1';

export interface Company {
  id?: number;
  name: string;
  industry?: string;
  website?: string;
  contactPerson?: string;
  contactEmail?: string;
}

export interface PlacementDrive {
  id: number;
  company: Company;
  role: string;
  packageLpa?: number;
  venue?: string;
  reportingTime?: string;
  instructions?: string;
  driveDate: string;
  deadline: string;
  status: 'DRAFT' | 'OPEN' | 'CLOSED' | 'COMPLETED';
}

export interface RuleEvaluation {
  ruleName: string;
  expected: any;
  actual: any;
  status: 'PASS' | 'FAIL' | 'MISSING';
  reason: string;
}

export interface EligibilityResultDto {
  studentId: number;
  driveId: number;
  overallEligible: boolean;
  evaluations: RuleEvaluation[];
}

export const placementService = {
  async getCompanies(): Promise<Company[]> {
    const res = await fetch(`${API_BASE}/companies`);
    if (!res.ok) throw new Error('Failed to fetch companies');
    return res.json();
  },

  async createCompany(companyData: Company): Promise<Company> {
    const res = await fetch(`${API_BASE}/companies`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(companyData),
    });
    if (res.status === 409) throw new Error('Company name already exists');
    if (!res.ok) throw new Error('Failed to create company');
    return res.json();
  },

  async getDrives(): Promise<PlacementDrive[]> {
    const res = await fetch(`${API_BASE}/drives`);
    if (!res.ok) throw new Error('Failed to fetch drives');
    return res.json();
  },

  async checkEligibility(driveId: number, studentId: number): Promise<EligibilityResultDto> {
    const res = await fetch(`${API_BASE}/drives/${driveId}/eligibility?studentId=${studentId}`);
    if (!res.ok) throw new Error('Failed to check eligibility');
    return res.json();
  },

  async applyToDrive(driveId: number, studentId: number): Promise<any> {
    const res = await fetch(`${API_BASE}/drives/${driveId}/applications?studentId=${studentId}`, {
      method: 'POST',
    });
    if (res.status === 409) throw new Error('Already applied to this drive');
    if (res.status === 403) throw new Error('Not eligible to apply');
    if (!res.ok) throw new Error('Failed to apply');
    return res.json();
  }
};