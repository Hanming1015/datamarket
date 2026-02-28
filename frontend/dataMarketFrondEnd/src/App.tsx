import { useState } from 'react';
import { Shield, ShoppingCart, FileCheck, DollarSign, Menu, X } from 'lucide-react';
import ConsentManagement from './pages/ConsentManagement';
import DataMarket from './pages/DataMarket';
import AuditLog from './pages/AuditLog';
import Billing from './pages/Billing';

type Page = 'consent' | 'market' | 'audit' | 'billing';

function App() {
  const [currentPage, setCurrentPage] = useState<Page>('consent');
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navigation = [
    { id: 'consent' as Page, name: 'Consent Management', icon: Shield, description: 'Data Owner View' },
    { id: 'market' as Page, name: 'Data Market', icon: ShoppingCart, description: 'Consumer View' },
    { id: 'audit' as Page, name: 'Audit Log', icon: FileCheck, description: 'Activity History' },
    { id: 'billing' as Page, name: 'Billing', icon: DollarSign, description: 'Usage & Revenue' }
  ];

  const renderPage = () => {
    switch (currentPage) {
      case 'consent':
        return <ConsentManagement />;
      case 'market':
        return <DataMarket />;
      case 'audit':
        return <AuditLog />;
      case 'billing':
        return <Billing />;
      default:
        return <ConsentManagement />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <nav className="bg-white border-b border-gray-200 sticky top-0 z-40 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-br from-blue-600 to-purple-600 rounded-lg flex items-center justify-center">
                  <Shield className="w-6 h-6 text-white" />
                </div>
                <div>
                  <h1 className="text-xl font-bold text-gray-900">ConsentHub</h1>
                  <p className="text-xs text-gray-600">Data Sharing Platform</p>
                </div>
              </div>
            </div>

            <div className="hidden md:flex items-center gap-2">
              {navigation.map((item) => {
                const Icon = item.icon;
                const isActive = currentPage === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => setCurrentPage(item.id)}
                    className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-all ${isActive
                        ? 'bg-blue-50 text-blue-700 font-semibold'
                        : 'text-gray-700 hover:bg-gray-100'
                      }`}
                  >
                    <Icon className="w-5 h-5" />
                    <div className="text-left">
                      <p className="text-sm">{item.name}</p>
                    </div>
                  </button>
                );
              })}
            </div>

            <div className="md:hidden flex items-center">
              <button
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                className="p-2 rounded-lg text-gray-600 hover:bg-gray-100"
              >
                {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
              </button>
            </div>
          </div>
        </div>

        {mobileMenuOpen && (
          <div className="md:hidden border-t border-gray-200 bg-white">
            <div className="px-4 py-3 space-y-2">
              {navigation.map((item) => {
                const Icon = item.icon;
                const isActive = currentPage === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => {
                      setCurrentPage(item.id);
                      setMobileMenuOpen(false);
                    }}
                    className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all ${isActive
                        ? 'bg-blue-50 text-blue-700 font-semibold'
                        : 'text-gray-700 hover:bg-gray-100'
                      }`}
                  >
                    <Icon className="w-5 h-5" />
                    <div className="text-left">
                      <p className="text-sm">{item.name}</p>
                      <p className="text-xs text-gray-500">{item.description}</p>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        )}
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex-grow w-full">
        {renderPage()}
      </main>

      <footer className="bg-white border-t border-gray-200 mt-auto">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <div className="text-sm text-gray-600">
              <span className="font-semibold">ConsentHub</span> - Consent-Driven Data Sharing Platform
            </div>
            <div className="flex items-center gap-4 text-xs text-gray-500">
              <span className="bg-green-100 text-green-700 px-2 py-1 rounded-full font-medium">GDPR Compliant</span>
              <span className="bg-blue-100 text-blue-700 px-2 py-1 rounded-full font-medium">Secure</span>
              <span className="bg-purple-100 text-purple-700 px-2 py-1 rounded-full font-medium">Transparent</span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;
