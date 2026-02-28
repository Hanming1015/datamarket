import { DataSet, ConsentRule, AccessRequest, AuditLog, BillingRecord } from '../types';

export const mockDataSets: DataSet[] = [
  {
    id: 'ds1',
    name: 'Cardiovascular Health Records',
    description: 'Comprehensive heart health data including ECG, blood pressure, and exercise metrics',
    fields: ['heart_rate', 'blood_pressure', 'ecg_data', 'exercise_duration', 'cholesterol_level'],
    owner: 'John Doe',
    createdAt: '2024-01-15',
    recordCount: 1250,
    category: 'health'
  },
  {
    id: 'ds2',
    name: 'Sleep Pattern Analysis',
    description: 'Detailed sleep tracking data with REM cycles and sleep quality metrics',
    fields: ['sleep_duration', 'rem_cycles', 'deep_sleep_time', 'awakenings', 'sleep_quality_score'],
    owner: 'John Doe',
    createdAt: '2024-02-20',
    recordCount: 890,
    category: 'health'
  },
  {
    id: 'ds3',
    name: 'Genomic Sequence Data',
    description: 'Personal genome sequencing results for research purposes',
    fields: ['gene_variants', 'snp_data', 'ancestry_markers', 'health_risks'],
    owner: 'John Doe',
    createdAt: '2024-03-10',
    recordCount: 3500000,
    category: 'genomic'
  },
  {
    id: 'ds4',
    name: 'Diabetes Management Records',
    description: 'Blood glucose levels, insulin usage, and dietary tracking',
    fields: ['glucose_level', 'insulin_dose', 'carb_intake', 'meal_timing', 'activity_level'],
    owner: 'Jane Smith',
    createdAt: '2024-01-05',
    recordCount: 2100,
    category: 'health'
  },
  {
    id: 'ds5',
    name: 'Mental Wellness Tracking',
    description: 'Mood patterns, stress levels, and cognitive performance metrics',
    fields: ['mood_score', 'stress_level', 'anxiety_index', 'sleep_quality', 'social_interaction'],
    owner: 'Jane Smith',
    createdAt: '2024-02-14',
    recordCount: 750,
    category: 'lifestyle'
  }
];

export const mockConsentRules: ConsentRule[] = [
  {
    id: 'cr1',
    datasetId: 'ds1',
    allowedRoles: ['Research Institution', 'University'],
    allowedPurposes: ['Medical Research', 'Clinical Trials'],
    allowedFields: ['heart_rate', 'blood_pressure', 'exercise_duration'],
    validFrom: '2024-01-15',
    validUntil: '2025-01-15',
    status: 'active',
    createdAt: '2024-01-15T10:00:00Z'
  },
  {
    id: 'cr2',
    datasetId: 'ds2',
    allowedRoles: ['Research Institution'],
    allowedPurposes: ['Sleep Research'],
    allowedFields: ['sleep_duration', 'rem_cycles', 'sleep_quality_score'],
    validFrom: '2024-02-20',
    validUntil: '2024-12-31',
    status: 'active',
    createdAt: '2024-02-20T14:30:00Z'
  },
  {
    id: 'cr3',
    datasetId: 'ds3',
    allowedRoles: ['Research Institution', 'Pharmaceutical Company'],
    allowedPurposes: ['Drug Development', 'Genetic Research'],
    allowedFields: ['gene_variants', 'health_risks'],
    validFrom: '2024-03-10',
    validUntil: '2026-03-10',
    status: 'active',
    createdAt: '2024-03-10T09:15:00Z'
  }
];

export const mockAccessRequests: AccessRequest[] = [
  {
    id: 'ar1',
    datasetId: 'ds1',
    datasetName: 'Cardiovascular Health Records',
    requesterId: 'req1',
    requesterName: 'Stanford Medical Research',
    purpose: 'Clinical Trials',
    requestedFields: ['heart_rate', 'blood_pressure', 'ecg_data'],
    status: 'approved',
    requestedAt: '2024-01-20T10:00:00Z',
    respondedAt: '2024-01-20T10:05:00Z'
  },
  {
    id: 'ar2',
    datasetId: 'ds2',
    datasetName: 'Sleep Pattern Analysis',
    requesterId: 'req2',
    requesterName: 'Sleep Research Foundation',
    purpose: 'Sleep Research',
    requestedFields: ['sleep_duration', 'rem_cycles', 'deep_sleep_time', 'sleep_quality_score'],
    status: 'partial',
    requestedAt: '2024-02-25T14:30:00Z',
    respondedAt: '2024-02-25T14:35:00Z'
  },
  {
    id: 'ar3',
    datasetId: 'ds3',
    datasetName: 'Genomic Sequence Data',
    requesterId: 'req3',
    requesterName: 'BioGen Pharmaceuticals',
    purpose: 'Drug Development',
    requestedFields: ['gene_variants', 'snp_data', 'health_risks'],
    status: 'pending',
    requestedAt: '2024-03-15T09:00:00Z'
  },
  {
    id: 'ar4',
    datasetId: 'ds4',
    datasetName: 'Diabetes Management Records',
    requesterId: 'req4',
    requesterName: 'Diabetes Care Institute',
    purpose: 'Insulin Optimization Study',
    requestedFields: ['glucose_level', 'insulin_dose', 'meal_timing'],
    status: 'approved',
    requestedAt: '2024-03-01T11:20:00Z',
    respondedAt: '2024-03-01T11:25:00Z'
  },
  {
    id: 'ar5',
    datasetId: 'ds1',
    datasetName: 'Cardiovascular Health Records',
    requesterId: 'req5',
    requesterName: 'HeartTech Analytics',
    purpose: 'AI Model Training',
    requestedFields: ['heart_rate', 'blood_pressure', 'ecg_data', 'cholesterol_level'],
    status: 'rejected',
    requestedAt: '2024-03-10T16:45:00Z',
    respondedAt: '2024-03-10T16:50:00Z'
  }
];

export const mockAuditLogs: AuditLog[] = [
  {
    id: 'al1',
    timestamp: '2024-03-15T09:00:00Z',
    userId: 'req3',
    userName: 'BioGen Pharmaceuticals',
    action: 'request_submitted',
    datasetId: 'ds3',
    datasetName: 'Genomic Sequence Data',
    details: 'Requested access to gene_variants, snp_data, health_risks for Drug Development'
  },
  {
    id: 'al2',
    timestamp: '2024-03-10T16:50:00Z',
    userId: 'owner1',
    userName: 'John Doe',
    action: 'request_rejected',
    datasetId: 'ds1',
    datasetName: 'Cardiovascular Health Records',
    details: 'Rejected request from HeartTech Analytics - Purpose not aligned with consent rules'
  },
  {
    id: 'al3',
    timestamp: '2024-03-10T14:22:00Z',
    userId: 'req1',
    userName: 'Stanford Medical Research',
    action: 'data_accessed',
    datasetId: 'ds1',
    datasetName: 'Cardiovascular Health Records',
    details: 'Accessed 150 records - Fields: heart_rate, blood_pressure'
  },
  {
    id: 'al4',
    timestamp: '2024-03-10T09:15:00Z',
    userId: 'owner1',
    userName: 'John Doe',
    action: 'consent_created',
    datasetId: 'ds3',
    datasetName: 'Genomic Sequence Data',
    details: 'Created consent rule for Research Institution, Pharmaceutical Company - Valid until 2026-03-10'
  },
  {
    id: 'al5',
    timestamp: '2024-03-01T11:25:00Z',
    userId: 'owner2',
    userName: 'Jane Smith',
    action: 'request_approved',
    datasetId: 'ds4',
    datasetName: 'Diabetes Management Records',
    details: 'Approved request from Diabetes Care Institute for Insulin Optimization Study'
  },
  {
    id: 'al6',
    timestamp: '2024-02-28T16:30:00Z',
    userId: 'req2',
    userName: 'Sleep Research Foundation',
    action: 'data_accessed',
    datasetId: 'ds2',
    datasetName: 'Sleep Pattern Analysis',
    details: 'Accessed 89 records - Fields: sleep_duration, rem_cycles, sleep_quality_score'
  },
  {
    id: 'al7',
    timestamp: '2024-02-25T14:35:00Z',
    userId: 'owner1',
    userName: 'John Doe',
    action: 'request_approved',
    datasetId: 'ds2',
    datasetName: 'Sleep Pattern Analysis',
    details: 'Partially approved request - Limited to sleep_duration, rem_cycles, sleep_quality_score'
  },
  {
    id: 'al8',
    timestamp: '2024-02-20T14:30:00Z',
    userId: 'owner1',
    userName: 'John Doe',
    action: 'consent_created',
    datasetId: 'ds2',
    datasetName: 'Sleep Pattern Analysis',
    details: 'Created consent rule for Research Institution - Sleep Research purpose'
  }
];

export const mockBillingRecords: BillingRecord[] = [
  {
    id: 'br1',
    userId: 'req1',
    userName: 'Stanford Medical Research',
    datasetId: 'ds1',
    datasetName: 'Cardiovascular Health Records',
    queryCount: 45,
    recordsAccessed: 6750,
    cost: 337.50,
    date: '2024-03-15'
  },
  {
    id: 'br2',
    userId: 'req2',
    userName: 'Sleep Research Foundation',
    datasetId: 'ds2',
    datasetName: 'Sleep Pattern Analysis',
    queryCount: 28,
    recordsAccessed: 2492,
    cost: 124.60,
    date: '2024-03-14'
  },
  {
    id: 'br3',
    userId: 'req4',
    userName: 'Diabetes Care Institute',
    datasetId: 'ds4',
    datasetName: 'Diabetes Management Records',
    queryCount: 62,
    recordsAccessed: 8990,
    cost: 449.50,
    date: '2024-03-13'
  },
  {
    id: 'br4',
    userId: 'req1',
    userName: 'Stanford Medical Research',
    datasetId: 'ds1',
    datasetName: 'Cardiovascular Health Records',
    queryCount: 38,
    recordsAccessed: 5700,
    cost: 285.00,
    date: '2024-03-12'
  },
  {
    id: 'br5',
    userId: 'req2',
    userName: 'Sleep Research Foundation',
    datasetId: 'ds2',
    datasetName: 'Sleep Pattern Analysis',
    queryCount: 19,
    recordsAccessed: 1691,
    cost: 84.55,
    date: '2024-03-11'
  }
];
