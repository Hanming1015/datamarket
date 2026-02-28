import { useState } from 'react';
import { DollarSign, TrendingUp, Database, FileText, Download, Calendar } from 'lucide-react';
import { mockBillingRecords, mockDataSets } from '../data/mockData';

export default function Billing() {
  const [viewMode, setViewMode] = useState<'consumer' | 'owner'>('consumer');
  const [timeRange, setTimeRange] = useState('30');

  const consumerStats = {
    totalQueries: mockBillingRecords.reduce((sum, record) => sum + record.queryCount, 0),
    totalRecords: mockBillingRecords.reduce((sum, record) => sum + record.recordsAccessed, 0),
    totalCost: mockBillingRecords.reduce((sum, record) => sum + record.cost, 0),
    avgCostPerQuery: mockBillingRecords.reduce((sum, record) => sum + record.cost, 0) /
                     mockBillingRecords.reduce((sum, record) => sum + record.queryCount, 0)
  };

  const ownerStats = {
    totalDatasets: mockDataSets.length,
    totalRecordsShared: 18932,
    totalRevenue: 1281.15,
    avgRevenuePerDataset: 1281.15 / mockDataSets.length
  };

  const monthlyTrend = [
    { month: 'Jan', consumer: 245, owner: 180 },
    { month: 'Feb', consumer: 312, owner: 245 },
    { month: 'Mar', consumer: 489, owner: 356 },
    { month: 'Apr', consumer: 567, owner: 412 },
    { month: 'May', consumer: 634, owner: 489 },
    { month: 'Jun', consumer: 721, owner: 567 }
  ];

  const topDatasets = [
    { name: 'Cardiovascular Health Records', queries: 83, revenue: 622.50, records: 12450 },
    { name: 'Diabetes Management Records', queries: 62, revenue: 449.50, records: 8990 },
    { name: 'Sleep Pattern Analysis', queries: 47, revenue: 209.15, records: 4183 }
  ];

  const maxTrendValue = Math.max(...monthlyTrend.map(m => viewMode === 'consumer' ? m.consumer : m.owner));

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Billing Dashboard</h1>
          <p className="text-gray-600 mt-1">Track usage, costs, and revenue</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="bg-white rounded-lg border border-gray-300 p-1 flex">
            <button
              onClick={() => setViewMode('consumer')}
              className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                viewMode === 'consumer'
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
            >
              Consumer View
            </button>
            <button
              onClick={() => setViewMode('owner')}
              className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                viewMode === 'owner'
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              }`}
            >
              Owner View
            </button>
          </div>
        </div>
      </div>

      {viewMode === 'consumer' ? (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <div className="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg p-6 text-white">
              <div className="flex items-center justify-between mb-2">
                <p className="text-blue-100 text-sm font-medium">Total Spend</p>
                <DollarSign className="w-5 h-5 text-blue-200" />
              </div>
              <p className="text-3xl font-bold mb-1">${consumerStats.totalCost.toFixed(2)}</p>
              <p className="text-blue-100 text-xs">Last 30 days</p>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-2">
                <p className="text-gray-600 text-sm font-medium">Total Queries</p>
                <Database className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-3xl font-bold text-gray-900 mb-1">{consumerStats.totalQueries}</p>
              <p className="text-green-600 text-xs flex items-center gap-1">
                <TrendingUp className="w-3 h-3" />
                +12% from last month
              </p>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-2">
                <p className="text-gray-600 text-sm font-medium">Records Accessed</p>
                <FileText className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-3xl font-bold text-gray-900 mb-1">{consumerStats.totalRecords.toLocaleString()}</p>
              <p className="text-green-600 text-xs flex items-center gap-1">
                <TrendingUp className="w-3 h-3" />
                +18% from last month
              </p>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-2">
                <p className="text-gray-600 text-sm font-medium">Avg Cost/Query</p>
                <DollarSign className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-3xl font-bold text-gray-900 mb-1">${consumerStats.avgCostPerQuery.toFixed(2)}</p>
              <p className="text-gray-500 text-xs">Per query average</p>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold text-gray-900">Spending Trend</h3>
                <select
                  value={timeRange}
                  onChange={(e) => setTimeRange(e.target.value)}
                  className="px-3 py-1 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="7">Last 7 days</option>
                  <option value="30">Last 30 days</option>
                  <option value="90">Last 90 days</option>
                </select>
              </div>

              <div className="space-y-4">
                {monthlyTrend.map((data, index) => (
                  <div key={data.month} className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-gray-600 font-medium w-12">{data.month}</span>
                      <span className="text-gray-900 font-semibold">${data.consumer}</span>
                    </div>
                    <div className="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
                      <div
                        className="bg-gradient-to-r from-blue-500 to-blue-600 h-full rounded-full transition-all duration-500"
                        style={{ width: `${(data.consumer / maxTrendValue) * 100}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
              <div className="space-y-3">
                <button className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                  <Download className="w-4 h-4" />
                  Download Invoice
                </button>
                <button className="w-full flex items-center justify-center gap-2 px-4 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors">
                  <Calendar className="w-4 h-4" />
                  View Payment History
                </button>
              </div>

              <div className="mt-6 p-4 bg-blue-50 rounded-lg border border-blue-100">
                <h4 className="text-sm font-semibold text-blue-900 mb-2">Payment Method</h4>
                <div className="flex items-center gap-3">
                  <div className="w-12 h-8 bg-gradient-to-r from-blue-600 to-purple-600 rounded flex items-center justify-center">
                    <span className="text-white text-xs font-bold">VISA</span>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-900">•••• 4242</p>
                    <p className="text-xs text-gray-600">Expires 12/25</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-900">Recent Transactions</h3>
              <button className="text-sm text-blue-600 hover:text-blue-700 font-medium">View All</button>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-3 px-4 text-sm font-semibold text-gray-700">Date</th>
                    <th className="text-left py-3 px-4 text-sm font-semibold text-gray-700">Dataset</th>
                    <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Queries</th>
                    <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Records</th>
                    <th className="text-right py-3 px-4 text-sm font-semibold text-gray-700">Cost</th>
                  </tr>
                </thead>
                <tbody>
                  {mockBillingRecords.map(record => (
                    <tr key={record.id} className="border-b border-gray-100 hover:bg-gray-50">
                      <td className="py-3 px-4 text-sm text-gray-900">
                        {new Date(record.date).toLocaleDateString()}
                      </td>
                      <td className="py-3 px-4 text-sm text-gray-900">{record.datasetName}</td>
                      <td className="py-3 px-4 text-sm text-gray-900 text-right">{record.queryCount}</td>
                      <td className="py-3 px-4 text-sm text-gray-900 text-right">
                        {record.recordsAccessed.toLocaleString()}
                      </td>
                      <td className="py-3 px-4 text-sm font-semibold text-gray-900 text-right">
                        ${record.cost.toFixed(2)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <div className="bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-lg p-6 text-white">
              <div className="flex items-center justify-between mb-2">
                <p className="text-green-100 text-sm font-medium">Total Revenue</p>
                <DollarSign className="w-5 h-5 text-green-200" />
              </div>
              <p className="text-3xl font-bold mb-1">${ownerStats.totalRevenue.toFixed(2)}</p>
              <p className="text-green-100 text-xs">Last 30 days</p>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-2">
                <p className="text-gray-600 text-sm font-medium">Active Datasets</p>
                <Database className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-3xl font-bold text-gray-900 mb-1">{ownerStats.totalDatasets}</p>
              <p className="text-green-600 text-xs flex items-center gap-1">
                <TrendingUp className="w-3 h-3" />
                +2 this month
              </p>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-2">
                <p className="text-gray-600 text-sm font-medium">Records Shared</p>
                <FileText className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-3xl font-bold text-gray-900 mb-1">{ownerStats.totalRecordsShared.toLocaleString()}</p>
              <p className="text-green-600 text-xs flex items-center gap-1">
                <TrendingUp className="w-3 h-3" />
                +24% from last month
              </p>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-2">
                <p className="text-gray-600 text-sm font-medium">Avg Revenue/Dataset</p>
                <DollarSign className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-3xl font-bold text-gray-900 mb-1">${ownerStats.avgRevenuePerDataset.toFixed(2)}</p>
              <p className="text-gray-500 text-xs">Per dataset average</p>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold text-gray-900">Revenue Trend</h3>
                <select
                  value={timeRange}
                  onChange={(e) => setTimeRange(e.target.value)}
                  className="px-3 py-1 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="7">Last 7 days</option>
                  <option value="30">Last 30 days</option>
                  <option value="90">Last 90 days</option>
                </select>
              </div>

              <div className="space-y-4">
                {monthlyTrend.map((data, index) => (
                  <div key={data.month} className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-gray-600 font-medium w-12">{data.month}</span>
                      <span className="text-gray-900 font-semibold">${data.owner}</span>
                    </div>
                    <div className="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
                      <div
                        className="bg-gradient-to-r from-green-500 to-green-600 h-full rounded-full transition-all duration-500"
                        style={{ width: `${(data.owner / maxTrendValue) * 100}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Payout Information</h3>
              <div className="space-y-4">
                <div className="p-4 bg-green-50 rounded-lg border border-green-100">
                  <p className="text-xs text-green-700 mb-1">Next Payout</p>
                  <p className="text-2xl font-bold text-green-900 mb-1">$1,281.15</p>
                  <p className="text-xs text-green-700">Scheduled for March 31, 2024</p>
                </div>

                <div className="pt-4 border-t border-gray-200">
                  <h4 className="text-sm font-semibold text-gray-900 mb-3">Bank Account</h4>
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 bg-gray-900 rounded flex items-center justify-center">
                      <span className="text-white text-xs font-bold">BANK</span>
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-900">•••• 9876</p>
                      <p className="text-xs text-gray-600">Chase Bank</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-900">Top Performing Datasets</h3>
            </div>
            <div className="space-y-4">
              {topDatasets.map((dataset, index) => (
                <div key={dataset.name} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 bg-green-100 rounded-lg flex items-center justify-center">
                        <span className="text-green-700 font-bold text-sm">#{index + 1}</span>
                      </div>
                      <h4 className="font-semibold text-gray-900">{dataset.name}</h4>
                    </div>
                    <span className="text-lg font-bold text-green-600">${dataset.revenue.toFixed(2)}</span>
                  </div>
                  <div className="grid grid-cols-3 gap-4 text-sm">
                    <div>
                      <p className="text-gray-600 text-xs mb-1">Queries</p>
                      <p className="font-semibold text-gray-900">{dataset.queries}</p>
                    </div>
                    <div>
                      <p className="text-gray-600 text-xs mb-1">Records Accessed</p>
                      <p className="font-semibold text-gray-900">{dataset.records.toLocaleString()}</p>
                    </div>
                    <div>
                      <p className="text-gray-600 text-xs mb-1">Avg per Query</p>
                      <p className="font-semibold text-gray-900">${(dataset.revenue / dataset.queries).toFixed(2)}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
}
