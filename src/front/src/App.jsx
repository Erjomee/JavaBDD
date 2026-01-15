import React, { useState, useEffect } from 'react';
import { Users, Briefcase, Plus, Edit, Trash2, X, RefreshCw, AlertCircle } from 'lucide-react';

const API_URL = 'http://localhost:8080/api';

const App = () => {
    const [activeTab, setActiveTab] = useState('programmers');
    const [programmers, setProgrammers] = useState([]);
    const [projects, setProjects] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [modalType, setModalType] = useState('');
    const [selectedId, setSelectedId] = useState(null);
    const [searchId, setSearchId] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [formData, setFormData] = useState({
        nom: '',
        prenom: '',
        anNaissance: '',
        salaire: '',
        prime: '',
        idProjet: ''
    });

    // Charger les données au démarrage
    useEffect(() => {
        loadProgrammers();
        loadProjects();
    }, []);

    const loadProgrammers = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`${API_URL}/programmeurs`);
            if (!response.ok) throw new Error('Erreur lors du chargement des programmeurs');
            const data = await response.json();
            // Mapper les noms de champs Java vers JavaScript
            const mappedData = data.map(p => ({
                id: p.idProgrammeur,
                nom: p.nom,
                prenom: p.prenom,
                anNaissance: p.anNaissance,
                salaire: p.salaire,
                prime: p.prime,
                idProjet: p.idProjet
            }));
            setProgrammers(mappedData);
        } catch (err) {
            setError(err.message);
            console.error('Erreur:', err);
        } finally {
            setLoading(false);
        }
    };

    const loadProjects = async () => {
        try {
            const response = await fetch(`${API_URL}/projets`);
            if (!response.ok) throw new Error('Erreur lors du chargement des projets');
            const data = await response.json();
            // Mapper les noms de champs Java vers JavaScript
            const mappedData = data.map(p => ({
                id: p.idProjet,
                intitule: p.nomProjet,
                etat: p.statut
            }));
            setProjects(mappedData);
        } catch (err) {
            console.error('Erreur:', err);
        }
    };

    const openModal = (type, id = null) => {
        setModalType(type);
        setSelectedId(id);

        if (type === 'salary' && id) {
            const programmer = programmers.find(p => p.id === id);
            if (programmer) {
                setFormData({ salaire: programmer.salaire.toString() });
            }
        } else {
            setFormData({
                nom: '',
                prenom: '',
                anNaissance: '',
                salaire: '',
                prime: '',
                idProjet: ''
            });
        }

        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setModalType('');
        setSelectedId(null);
        setFormData({
            nom: '',
            prenom: '',
            anNaissance: '',
            salaire: '',
            prime: '',
            idProjet: ''
        });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleAddProgrammer = async () => {
        try {
            const newProgrammer = {
                nom: formData.nom,
                prenom: formData.prenom,
                anNaissance: parseInt(formData.anNaissance),
                salaire: parseFloat(formData.salaire),
                prime: parseFloat(formData.prime),
                idProjet: parseInt(formData.idProjet)
            };

            const response = await fetch(`${API_URL}/programmeurs`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newProgrammer)
            });

            if (!response.ok) throw new Error('Erreur lors de l\'ajout');

            await loadProgrammers();
            closeModal();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    const handleUpdateSalary = async () => {
        try {
            const response = await fetch(`${API_URL}/programmeurs/${selectedId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ salaire: parseFloat(formData.salaire) })
            });

            if (!response.ok) throw new Error('Erreur lors de la modification');

            await loadProgrammers();
            closeModal();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    const handleDeleteProgrammer = async (id) => {
        if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce programmeur ?')) return;

        try {
            const response = await fetch(`${API_URL}/programmeurs/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Erreur lors de la suppression');

            await loadProgrammers();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    const getProjectName = (idProjet) => {
        const project = projects.find(p => p.id === idProjet);
        return project ? project.intitule : 'N/A';
    };

    const getProgrammersByProject = (projectId) => {
        return programmers.filter(p => p.idProjet === projectId);
    };

    const filteredProgrammer = searchId
        ? programmers.find(p => p.id === parseInt(searchId))
        : null;

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
            <div className="container mx-auto px-4 py-8">
                <div className="bg-white rounded-2xl shadow-2xl overflow-hidden">
                    {/* Header */}
                    <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-8 text-white">
                        <div className="flex justify-between items-center">
                            <div>
                                <h1 className="text-4xl font-bold mb-2">Système de Gestion</h1>
                                <p className="text-blue-100">Gestion des programmeurs et projets</p>
                            </div>
                            <button
                                onClick={() => {
                                    loadProgrammers();
                                    loadProjects();
                                }}
                                className="flex items-center gap-2 px-4 py-2 bg-white bg-opacity-20 hover:bg-opacity-30 rounded-lg transition"
                                title="Actualiser"
                            >
                                <RefreshCw size={20} />
                                Actualiser
                            </button>
                        </div>
                    </div>

                    {/* Error message */}
                    {error && (
                        <div className="mx-8 mt-4 bg-red-50 border border-red-200 rounded-lg p-4 flex items-center gap-3 text-red-700">
                            <AlertCircle size={20} />
                            <div>
                                <strong>Erreur:</strong> {error}
                                <p className="text-sm mt-1">Assurez-vous que le backend est démarré sur http://localhost:8080</p>
                            </div>
                        </div>
                    )}

                    {/* Navigation Tabs */}
                    <div className="flex border-b border-gray-200 bg-gray-50">
                        <button
                            onClick={() => setActiveTab('programmers')}
                            className={`flex items-center gap-2 px-8 py-4 font-semibold transition-all ${
                                activeTab === 'programmers'
                                    ? 'bg-white text-blue-600 border-b-2 border-blue-600'
                                    : 'text-gray-600 hover:text-blue-600 hover:bg-gray-100'
                            }`}
                        >
                            <Users size={20} />
                            Programmeurs ({programmers.length})
                        </button>
                        <button
                            onClick={() => setActiveTab('projects')}
                            className={`flex items-center gap-2 px-8 py-4 font-semibold transition-all ${
                                activeTab === 'projects'
                                    ? 'bg-white text-blue-600 border-b-2 border-blue-600'
                                    : 'text-gray-600 hover:text-blue-600 hover:bg-gray-100'
                            }`}
                        >
                            <Briefcase size={20} />
                            Projets ({projects.length})
                        </button>
                    </div>

                    {/* Content */}
                    <div className="p-8">
                        {loading ? (
                            <div className="text-center py-12">
                                <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                                <p className="mt-4 text-gray-600">Chargement...</p>
                            </div>
                        ) : activeTab === 'programmers' ? (
                            <div>
                                {/* Search and Add Bar */}
                                <div className="flex gap-4 mb-6">
                                    <div className="flex-1 flex gap-2">
                                        <input
                                            type="number"
                                            placeholder="Rechercher par ID..."
                                            value={searchId}
                                            onChange={(e) => setSearchId(e.target.value)}
                                            className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                        <button
                                            onClick={() => setSearchId('')}
                                            className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition"
                                        >
                                            <X size={20} />
                                        </button>
                                    </div>
                                    <button
                                        onClick={() => openModal('add')}
                                        className="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-semibold"
                                    >
                                        <Plus size={20} />
                                        Ajouter
                                    </button>
                                </div>

                                {/* Programmers List */}
                                {searchId && filteredProgrammer ? (
                                    <div className="bg-blue-50 border border-blue-200 rounded-xl p-6 mb-4">
                                        <h3 className="text-xl font-bold text-blue-900 mb-4">Résultat de la recherche</h3>
                                        <div className="grid grid-cols-2 gap-3 text-sm">
                                            <div><span className="font-semibold">ID:</span> {filteredProgrammer.id}</div>
                                            <div><span className="font-semibold">Nom:</span> {filteredProgrammer.nom}</div>
                                            <div><span className="font-semibold">Prénom:</span> {filteredProgrammer.prenom}</div>
                                            <div><span className="font-semibold">Année:</span> {filteredProgrammer.anNaissance}</div>
                                            <div><span className="font-semibold">Salaire:</span> {filteredProgrammer.salaire.toLocaleString()} €</div>
                                            <div><span className="font-semibold">Prime:</span> {filteredProgrammer.prime.toLocaleString()} €</div>
                                            <div className="col-span-2"><span className="font-semibold">Projet:</span> {getProjectName(filteredProgrammer.idProjet)}</div>
                                        </div>
                                    </div>
                                ) : searchId && !filteredProgrammer ? (
                                    <div className="bg-red-50 border border-red-200 rounded-xl p-4 mb-4 text-red-700 text-center">
                                        Aucun programmeur trouvé avec l'ID {searchId}
                                    </div>
                                ) : null}

                                <div className="grid gap-4">
                                    {programmers.map((programmer) => (
                                        <div
                                            key={programmer.id}
                                            className="bg-gradient-to-r from-white to-gray-50 border border-gray-200 rounded-xl p-6 hover:shadow-lg transition"
                                        >
                                            <div className="flex justify-between items-start">
                                                <div className="flex-1">
                                                    <div className="flex items-center gap-3 mb-3">
                                                        <span className="bg-blue-600 text-white px-3 py-1 rounded-full text-sm font-bold">
                                                            ID: {programmer.id}
                                                        </span>
                                                        <h3 className="text-xl font-bold text-gray-800">
                                                            {programmer.prenom} {programmer.nom}
                                                        </h3>
                                                    </div>
                                                    <div className="grid grid-cols-3 gap-4 text-sm">
                                                        <div>
                                                            <span className="text-gray-500">Année:</span>
                                                            <div className="font-semibold text-gray-800">{programmer.anNaissance}</div>
                                                        </div>
                                                        <div>
                                                            <span className="text-gray-500">Salaire:</span>
                                                            <div className="font-semibold text-green-600">{programmer.salaire.toLocaleString()} €</div>
                                                        </div>
                                                        <div>
                                                            <span className="text-gray-500">Prime:</span>
                                                            <div className="font-semibold text-green-600">{programmer.prime.toLocaleString()} €</div>
                                                        </div>
                                                    </div>
                                                    <div className="mt-3">
                                                        <span className="text-gray-500 text-sm">Projet:</span>
                                                        <div className="inline-block ml-2 px-3 py-1 bg-indigo-100 text-indigo-700 rounded-lg text-sm font-medium">
                                                            {getProjectName(programmer.idProjet)}
                                                        </div>
                                                    </div>
                                                </div>
                                                <div className="flex gap-2">
                                                    <button
                                                        onClick={() => openModal('salary', programmer.id)}
                                                        className="p-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition"
                                                        title="Modifier salaire"
                                                    >
                                                        <Edit size={18} />
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteProgrammer(programmer.id)}
                                                        className="p-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition"
                                                        title="Supprimer"
                                                    >
                                                        <Trash2 size={18} />
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        ) : (
                            <div className="grid gap-6">
                                {projects.map((project) => {
                                    const projectProgrammers = getProgrammersByProject(project.id);
                                    return (
                                        <div
                                            key={project.id}
                                            className="bg-gradient-to-r from-white to-indigo-50 border border-indigo-200 rounded-xl p-6 hover:shadow-lg transition"
                                        >
                                            <div className="flex justify-between items-start mb-4">
                                                <div>
                                                    <div className="flex items-center gap-3 mb-2">
                                                        <span className="bg-indigo-600 text-white px-3 py-1 rounded-full text-sm font-bold">
                                                            ID: {project.id}
                                                        </span>
                                                        <h3 className="text-2xl font-bold text-gray-800">{project.intitule}</h3>
                                                    </div>
                                                    <span className={`inline-block px-3 py-1 rounded-lg text-sm font-semibold ${
                                                        project.etat === 'En cours'
                                                            ? 'bg-yellow-100 text-yellow-800'
                                                            : 'bg-green-100 text-green-800'
                                                    }`}>
                                                        {project.etat}
                                                    </span>
                                                </div>
                                            </div>

                                            <div className="border-t border-indigo-200 pt-4 mt-4">
                                                <h4 className="font-semibold text-gray-700 mb-3 flex items-center gap-2">
                                                    <Users size={18} />
                                                    Programmeurs assignés ({projectProgrammers.length})
                                                </h4>
                                                {projectProgrammers.length > 0 ? (
                                                    <div className="grid grid-cols-2 gap-3">
                                                        {projectProgrammers.map((prog) => (
                                                            <div
                                                                key={prog.id}
                                                                className="bg-white border border-indigo-100 rounded-lg p-3 hover:bg-indigo-50 transition"
                                                            >
                                                                <div className="font-semibold text-gray-800">
                                                                    {prog.prenom} {prog.nom}
                                                                </div>
                                                                <div className="text-sm text-gray-500">
                                                                    Salaire: {prog.salaire.toLocaleString()} €
                                                                </div>
                                                            </div>
                                                        ))}
                                                    </div>
                                                ) : (
                                                    <div className="text-gray-500 italic text-center py-4 bg-gray-50 rounded-lg">
                                                        Aucun programmeur assigné à ce projet
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Modal */}
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
                        <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-6 text-white">
                            <h2 className="text-2xl font-bold">
                                {modalType === 'add' && 'Ajouter un programmeur'}
                                {modalType === 'salary' && 'Modifier le salaire'}
                            </h2>
                        </div>

                        <div className="p-6">
                            {modalType === 'add' && (
                                <div className="space-y-4">
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Nom</label>
                                        <input
                                            type="text"
                                            name="nom"
                                            value={formData.nom}
                                            onChange={handleInputChange}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Prénom</label>
                                        <input
                                            type="text"
                                            name="prenom"
                                            value={formData.prenom}
                                            onChange={handleInputChange}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Année de naissance</label>
                                        <input
                                            type="number"
                                            name="anNaissance"
                                            value={formData.anNaissance}
                                            onChange={handleInputChange}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Salaire</label>
                                        <input
                                            type="number"
                                            name="salaire"
                                            value={formData.salaire}
                                            onChange={handleInputChange}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Prime</label>
                                        <input
                                            type="number"
                                            name="prime"
                                            value={formData.prime}
                                            onChange={handleInputChange}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Projet</label>
                                        <select
                                            name="idProjet"
                                            value={formData.idProjet}
                                            onChange={handleInputChange}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        >
                                            <option value="">Sélectionner un projet</option>
                                            {projects.map(project => (
                                                <option key={project.id} value={project.id}>
                                                    {project.intitule}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                </div>
                            )}

                            {modalType === 'salary' && (
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-2">Nouveau salaire</label>
                                    <input
                                        type="number"
                                        name="salaire"
                                        value={formData.salaire}
                                        onChange={handleInputChange}
                                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                </div>
                            )}

                            <div className="flex gap-3 mt-6">
                                <button
                                    onClick={closeModal}
                                    className="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition font-semibold"
                                >
                                    Annuler
                                </button>
                                <button
                                    onClick={modalType === 'add' ? handleAddProgrammer : handleUpdateSalary}
                                    className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-semibold"
                                >
                                    {modalType === 'add' ? 'Ajouter' : 'Modifier'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default App;