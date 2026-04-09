import { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Database, Loader, X } from 'lucide-react';
import { datasetApi } from '../services/api';
import { Toast } from '../components/Toast';

interface Dataset {
  id: string;
  name: string;
  description: string;
  category: string;
  recordCount: number;
  fieldsSchema: { name: string }[];
  fields: string[];
  createdAt: string;
}

export default function DatasetManagement({ user }: { user: any }) {
  const [datasets, setDatasets] = useState<Dataset[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingDataset, setEditingDataset] = useState<Dataset | null>(null);
  const [toast, setToast] = useState<{ show: boolean; message: string; type: 'success' | 'error' | 'info' }>({ show: false, message: '', type: 'info' });

  // Form State
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    category: '',
    recordCount: 0,
    fieldsSchemaStr: ''
  });

  const fetchDatasets = async () => {
    try {
      setLoading(true);
      const res = await datasetApi.list();
      setDatasets(res.data || []);
    } catch (error) {
      console.error('Failed to fetch datasets:', error);
      setToast({ show: true, message: 'Failed to load datasets', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDatasets();
  }, []);

  const handleOpenModal = (dataset?: Dataset) => {
    if (dataset) {
      setEditingDataset(dataset);
      setFormData({
        name: dataset.name,
        description: dataset.description,
        category: dataset.category,
        recordCount: dataset.recordCount,
        fieldsSchemaStr: dataset.fieldsSchema?.map(f => f.name).join(', ') || ''
      });
    } else {
      setEditingDataset(null);
      setFormData({ name: '', description: '', category: '', recordCount: 0, fieldsSchemaStr: '' });
    }
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingDataset(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const fieldsSchema = formData.fieldsSchemaStr.split(',').map(s => ({ name: s.trim() })).filter(f => f.name);

    const payload = {
      name: formData.name,
      description: formData.description,
      category: formData.category,
      recordCount: Number(formData.recordCount),
      fieldsSchema
    };

    try {
      if (editingDataset) {
        await datasetApi.update({ ...payload, id: editingDataset.id });
        setToast({ show: true, message: 'Dataset updated successfully', type: 'success' });
      } else {
        await datasetApi.add(payload);
        setToast({ show: true, message: 'Dataset added successfully', type: 'success' });
      }
      handleCloseModal();
      fetchDatasets();
    } catch (error) {
      console.error('Save failed:', error);
      setToast({ show: true, message: 'Failed to save dataset', type: 'error' });
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this dataset?')) return;
    try {
      await datasetApi.remove(id);
      setToast({ show: true, message: 'Dataset deleted successfully', type: 'success' });
      fetchDatasets();
    } catch (error) {
      console.error('Delete failed:', error);
      setToast({ show: true, message: 'Failed to delete dataset', type: 'error' });
    }
  };

  if (!user || (user.role?.toUpperCase() !== 'OWNER' && user.role?.toUpperCase() !== 'ADMIN' && user.role !== 'data_owner')) {
    return (
      <div className="p-8 text-center text-gray-500">
        <h2 className="text-xl font-semibold mb-2">Access Denied</h2>
        <p>Only Data Owners can manage datasets. current role: {user?.role}</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {toast.show && (
        <Toast message={toast.message} type={toast.type} onClose={() => setToast({ ...toast, show: false })} />
      )}

      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Database className="w-6 h-6 text-blue-600" />
            Dataset Management
          </h1>
          <p className="text-gray-600 mt-1">Manage your data assets available for sharing</p>
        </div>
        <button
          onClick={() => handleOpenModal()}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          Add Dataset
        </button>
      </div>

      {loading ? (
        <div className="flex justify-center items-center py-12">
          <Loader className="w-8 h-8 animate-spin text-blue-600" />
        </div>
      ) : datasets.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm p-12 text-center border md:col-span-2 lg:col-span-3">
          <Database className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No Datasets Found</h3>
          <p className="text-gray-500 mb-6">You haven't added any datasets yet.</p>
          <button
            onClick={() => handleOpenModal()}
            className="text-blue-600 font-medium hover:text-blue-700"
          >
            Create your first dataset
          </button>
        </div>
      ) : (
        <div className="bg-white shadow overflow-hidden sm:rounded-md border border-gray-200">
          <ul className="divide-y divide-gray-200">
            {datasets.map((dataset) => (
              <li key={dataset.id}>
                <div className="px-5 py-5 sm:px-8 hover:bg-gray-50 transition border-l-4 border-transparent hover:border-blue-500">
                  <div className="flex items-start justify-between">
                    <div className="flex flex-col max-w-4xl">
                      <div className="flex items-center gap-2 mb-1">
                        <Database className="w-5 h-5 text-blue-600" />
                        <h3 className="text-lg font-bold text-gray-900 tracking-tight">{dataset.name}</h3>
                      </div>
                      <p className="text-sm text-gray-500 mt-1.5 leading-relaxed">{dataset.description}</p>
                    </div>
                    <div className="ml-4 flex-shrink-0 flex gap-1">
                      <button onClick={() => handleOpenModal(dataset)} className="p-2.5 text-gray-400 hover:text-blue-600 rounded-full hover:bg-blue-100 transition duration-200" title="Edit Dataset">
                        <Edit2 className="w-4 h-4" />
                      </button>
                      <button onClick={() => handleDelete(dataset.id)} className="p-2.5 text-gray-400 hover:text-red-600 rounded-full hover:bg-red-100 transition duration-200" title="Delete Dataset">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                  <div className="mt-4 flex flex-col gap-3">
                    <div className="flex flex-col sm:flex-row sm:justify-between gap-2">
                      <div className="flex flex-wrap gap-4">
                        <p className="flex items-center text-sm text-gray-500">
                          Category: <span className="ml-2 px-2 py-0.5 bg-blue-50 text-blue-700 border border-blue-100 rounded-full font-medium text-xs capitalize">{dataset.category}</span>
                        </p>
                        <p className="flex items-center text-sm text-gray-500">
                          Records: <span className="ml-2 font-medium text-gray-700">{dataset.recordCount.toLocaleString()}</span>
                        </p>
                      </div>
                      <div className="flex items-center text-sm text-gray-500">
                        Created: {new Date(dataset.createdAt).toLocaleDateString()}
                      </div>
                    </div>
                    <div className="flex items-start mt-1">
                      <span className="text-sm text-gray-500 mr-3 mt-0.5 whitespace-nowrap">Fields:</span>
                      <div className="flex flex-wrap gap-1.5">
                        {dataset.fields && dataset.fields.length > 0 ? (
                          dataset.fields.map((field, idx) => (
                            <span key={idx} className="px-2 py-0.5 bg-gray-100/80 border border-gray-200 text-gray-600 rounded-md text-xs font-mono shadow-sm">
                              {field}
                            </span>
                          ))
                        ) : (
                          <span className="text-sm text-gray-500">-</span>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full overflow-hidden">
            <div className="flex justify-between items-center p-6 border-b">
              <h2 className="text-xl font-semibold">{editingDataset ? 'Edit Dataset' : 'Add New Dataset'}</h2>
              <button onClick={handleCloseModal} className="text-gray-500 hover:bg-gray-100 p-2 rounded-full">
                <X className="w-5 h-5" />
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                <input required type="text" className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500" value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                <textarea required className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500" value={formData.description} onChange={e => setFormData({ ...formData, description: e.target.value })} rows={2} />
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
                  <input required type="text" className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500" value={formData.category} onChange={e => setFormData({ ...formData, category: e.target.value })} placeholder="e.g. Healthcare"/>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Record Count</label>
                  <input required type="number" min="0" className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500" value={formData.recordCount} onChange={e => setFormData({ ...formData, recordCount: Number(e.target.value) })} />
                </div>
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Fields / Schema (comma separated)</label>
                <input required type="text" className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500" value={formData.fieldsSchemaStr} onChange={e => setFormData({ ...formData, fieldsSchemaStr: e.target.value })} placeholder="e.g. id, name, age, city" />
                <p className="text-xs text-gray-500 mt-1">Specify column names available in this dataset</p>
              </div>
              
              <div className="pt-4 flex justify-end gap-3 border-t">
                <button type="button" onClick={handleCloseModal} className="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg font-medium">Cancel</button>
                <button type="submit" className="px-4 py-2 bg-blue-600 text-white hover:bg-blue-700 rounded-lg font-medium">{editingDataset ? 'Save Changes' : 'Add Dataset'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
