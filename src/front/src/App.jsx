import React, { useState, useEffect } from 'react';
import { Users, Briefcase, Plus, Trash2, X, RefreshCw, AlertCircle, Pencil, UserMinus } from 'lucide-react';

const API_URL = 'http://localhost:8080/api';

const App = () => {
    const [activeTab, setActiveTab] = useState('programmers');
    const [programmers, setProgrammers] = useState([]);
    const [projects, setProjects] = useState([]);
    const [showModal, setShowModal] = useState(false);

    // 'addProg' | 'editSalary' | 'editPrime' | 'editProject' | 'addProject'
    const [modalType, setModalType] = useState('');
    const [selectedId, setSelectedId] = useState(null);

    const [searchId, setSearchId] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Formulaires
    const [formData, setFormData] = useState({
        // programmeur
        nom: '',
        prenom: '',
        anNaissance: '',
        salaire: '',
        prime: '',
        idProjet: '',

        // projet
        nom_projet: '',
        dateDebut: '',
        dateFin: '',
        statut: 'En cours'
    });

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

            const mappedData = data.map(p => ({
                id: p.idProgrammeur,
                nom: p.nom,
                prenom: p.prenom,
                anNaissance: p.anNaissance,
                salaire: p.salaire,
                prime: p.prime,
                idProjet: p.idProjet // 0 = pas de projet
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

            const mappedData = data.map(p => ({
                id: p.idProjet,
                nom_projet: p.nom_projet,
                dateDebut: p.dateDebut, // "YYYY-MM-DD" ou ""
                dateFin: p.dateFin,     // "YYYY-MM-DD" ou ""
                statut: p.statut
            }));

            setProjects(mappedData);
        } catch (err) {
            console.error('Erreur:', err);
        }
    };

    const openModal = (type, id = null) => {
        setModalType(type);
        setSelectedId(id);

        // Reset léger + préremplissage selon le type
        if (type === 'addProg') {
            setFormData(prev => ({
                ...prev,
                nom: '',
                prenom: '',
                anNaissance: '',
                salaire: '',
                prime: '',
                idProjet: ''
            }));
        }

        if ((type === 'editSalary' || type === 'editPrime' || type === 'editProject') && id) {
            const programmer = programmers.find(p => p.id === id);
            if (programmer) {
                if (type === 'editSalary') {
                    setFormData(prev => ({ ...prev, salaire: String(programmer.salaire) }));
                } else if (type === 'editPrime') {
                    setFormData(prev => ({ ...prev, prime: String(programmer.prime) }));
                } else if (type === 'editProject') {
                    setFormData(prev => ({ ...prev, idProjet: programmer.idProjet === 0 ? '' : String(programmer.idProjet) }));
                }
            }
        }

        if (type === 'addProject') {
            setFormData(prev => ({
                ...prev,
                nom_projet: '',
                dateDebut: '',
                dateFin: '',
                statut: 'En cours'
            }));
        }

        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setModalType('');
        setSelectedId(null);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    // ---------------- API actions ----------------

    const handleAddProgrammer = async () => {
        try {
            const newProgrammer = {
                nom: formData.nom,
                prenom: formData.prenom,
                anNaissance: parseInt(formData.anNaissance, 10),
                salaire: parseFloat(formData.salaire),
                prime: parseFloat(formData.prime),
                idProjet: formData.idProjet === '' ? 0 : parseInt(formData.idProjet, 10)
            };

            const response = await fetch(`${API_URL}/programmeurs`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newProgrammer)
            });

            if (!response.ok) throw new Error('Erreur lors de l\'ajout');

            await loadProgrammers();
            await loadProjects();
            closeModal();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    // EXISTANT backend: PUT /programmeurs/{id} avec {"salaire":...}
    const handleUpdateSalary = async () => {
        try {
            const response = await fetch(`${API_URL}/programmeurs/${selectedId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ salaire: parseFloat(formData.salaire) })
            });

            if (!response.ok) throw new Error('Erreur lors de la modification du salaire');

            await loadProgrammers();
            closeModal();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    // AJOUT backend: PUT /programmeurs/{id}/prime avec {"prime":...}
    const handleUpdatePrime = async () => {
        try {
            const response = await fetch(`${API_URL}/programmeurs/${selectedId}/prime`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ prime: parseFloat(formData.prime) })
            });

            if (!response.ok) throw new Error('Erreur lors de la modification de la prime');

            await loadProgrammers();
            closeModal();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    // AJOUT backend: PUT /programmeurs/{id}/projet avec {"idProjet":...}
    // idProjet: 0 => retirer du projet
    const handleUpdateProject = async (forcedIdProjet = null) => {
        try {
            const idProjetValue =
                forcedIdProjet !== null
                    ? forcedIdProjet
                    : (formData.idProjet === '' ? 0 : parseInt(formData.idProjet, 10));

            const response = await fetch(`${API_URL}/programmeurs/${selectedId}/projet`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ idProjet: idProjetValue })
            });

            if (!response.ok) throw new Error('Erreur lors de la modification du projet');

            await loadProgrammers();
            await loadProjects();
            closeModal();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    // Nouvelle fonction pour retirer un programmeur d'un projet
    const handleRemoveFromProject = async (programmerId) => {
        if (!window.confirm('Voulez-vous vraiment retirer ce programmeur du projet ?')) return;

        try {
            const response = await fetch(`${API_URL}/programmeurs/${programmerId}/projet`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ idProjet: 0 })
            });

            if (!response.ok) throw new Error('Erreur lors du retrait du projet');

            await loadProgrammers();
            await loadProjects();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    const handleDeleteProgrammer = async (id) => {
        if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce programmeur ?')) return;

        try {
            const response = await fetch(`${API_URL}/programmeurs/${id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error('Erreur lors de la suppression');

            await loadProgrammers();
            await loadProjects();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    // AJOUT projet : POST /projets
    const handleAddProject = async () => {
        try {
            const payload = {
                nom_projet: formData.nom_projet,
                dateDebut: formData.dateDebut, // "YYYY-MM-DD" ou ""
                dateFin: formData.dateFin,
                statut: formData.statut
            };

            const response = await fetch(`${API_URL}/projets`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) throw new Error('Erreur lors de l\'ajout du projet');

            await loadProjects();
            closeModal();
        } catch (err) {
            alert('Erreur: ' + err.message);
        }
    };

    // Helpers
    const getProjectName = (idProjet) => {
        if (!idProjet || idProjet === 0) return "Aucun";
        const project = projects.find(p => p.id === idProjet);
        return project ? project.nom_projet : "Aucun";
    };

    const getProgrammersByProject = (projectId) => programmers.filter(p => p.idProjet === projectId);

    const filteredProgrammer = searchId
        ? programmers.find(p => p.id === parseInt(searchId, 10))
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
                                onClick={() => { loadProgrammers(); loadProjects(); }}
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

                    {/* Tabs */}
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
                                {/* Search + add */}
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
                                            title="Effacer"
                                        >
                                            <X size={20} />
                                        </button>
                                    </div>

                                    <button
                                        onClick={() => openModal('addProg')}
                                        className="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-semibold"
                                    >
                                        <Plus size={20} />
                                        Ajouter
                                    </button>
                                </div>

                                {/* Search result */}
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

                                {/* List */}
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
                                                            <div
                                                                className="font-semibold text-green-600 inline-flex items-center gap-1 cursor-pointer hover:underline"
                                                                onClick={() => openModal('editSalary', programmer.id)}
                                                                title="Modifier le salaire"
                                                            >
                                                                {programmer.salaire.toLocaleString()} € <Pencil size={14} />
                                                            </div>
                                                        </div>

                                                        <div>
                                                            <span className="text-gray-500">Prime:</span>
                                                            <div
                                                                className="font-semibold text-green-600 inline-flex items-center gap-1 cursor-pointer hover:underline"
                                                                onClick={() => openModal('editPrime', programmer.id)}
                                                                title="Modifier la prime"
                                                            >
                                                                {programmer.prime.toLocaleString()} € <Pencil size={14} />
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div className="mt-3">
                                                        <span className="text-gray-500 text-sm">Projet:</span>
                                                        <div
                                                            className="inline-flex items-center gap-1 ml-2 px-3 py-1 bg-indigo-100 text-indigo-700 rounded-lg text-sm font-medium cursor-pointer hover:underline"
                                                            onClick={() => openModal('editProject', programmer.id)}
                                                            title="Modifier le projet"
                                                        >
                                                            {getProjectName(programmer.idProjet)} <Pencil size={14} />
                                                        </div>
                                                    </div>
                                                </div>

                                                <div className="flex gap-2">
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
                            <div>
                                {/* Add project */}
                                <div className="flex justify-end mb-6">
                                    <button
                                        onClick={() => openModal('addProject')}
                                        className="flex items-center gap-2 px-6 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition font-semibold"
                                    >
                                        <Plus size={20} />
                                        Ajouter un projet
                                    </button>
                                </div>

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
                                                            <h3 className="text-2xl font-bold text-gray-800">{project.nom_projet}</h3>
                                                        </div>

                                                        <div className="text-sm text-gray-700">
                                                            <div><span className="font-semibold">Début :</span> {project.dateDebut || '—'}</div>
                                                            <div><span className="font-semibold">Fin :</span> {project.dateFin || '—'}</div>
                                                        </div>

                                                        <span className={`inline-block mt-2 px-3 py-1 rounded-lg text-sm font-semibold ${
                                                            project.statut === 'En cours'
                                                                ? 'bg-yellow-100 text-yellow-800'
                                                                : 'bg-green-100 text-green-800'
                                                        }`}>
                                  {project.statut}
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
                                                                    className="bg-white border border-indigo-100 rounded-lg p-3 hover:bg-indigo-50 transition flex items-center justify-between"
                                                                >
                                                                    <div>
                                                                        <div className="font-semibold text-gray-800">
                                                                            {prog.prenom} {prog.nom}
                                                                        </div>
                                                                        <div className="text-sm text-gray-500">
                                                                            Salaire: {prog.salaire.toLocaleString()} €
                                                                        </div>
                                                                    </div>

                                                                    <button
                                                                        onClick={() => handleRemoveFromProject(prog.id)}
                                                                        className="p-2 bg-orange-100 text-orange-700 rounded-lg hover:bg-orange-200 transition"
                                                                        title="Retirer du projet"
                                                                    >
                                                                        <UserMinus size={18} />
                                                                    </button>
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
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* MODAL */}
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
                        <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-6 text-white">
                            <h2 className="text-2xl font-bold">
                                {modalType === 'addProg' && 'Ajouter un programmeur'}
                                {modalType === 'editSalary' && 'Modifier le salaire'}
                                {modalType === 'editPrime' && 'Modifier la prime'}
                                {modalType === 'editProject' && 'Modifier le projet'}
                                {modalType === 'addProject' && 'Ajouter un projet'}
                            </h2>
                        </div>

                        <div className="p-6">
                            {modalType === 'addProg' && (
                                <div className="space-y-4">
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Nom</label>
                                        <input type="text" name="nom" value={formData.nom} onChange={handleInputChange}
                                               className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Prénom</label>
                                        <input type="text" name="prenom" value={formData.prenom} onChange={handleInputChange}
                                               className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Année de naissance</label>
                                        <input type="number" name="anNaissance" value={formData.anNaissance} onChange={handleInputChange}
                                               className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Salaire</label>
                                        <input type="number" name="salaire" value={formData.salaire} onChange={handleInputChange}
                                               className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Prime</label>
                                        <input type="number" name="prime" value={formData.prime} onChange={handleInputChange}
                                               className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Projet</label>
                                        <select name="idProjet" value={formData.idProjet} onChange={handleInputChange}
                                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                                            <option value="">Non assigné</option>
                                            {projects.map(project => (
                                                <option key={project.id} value={project.id}>{project.nom_projet}</option>
                                            ))}
                                        </select>
                                    </div>
                                </div>
                            )}

                            {modalType === 'editSalary' && (
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-2">Nouveau salaire</label>
                                    <input type="number" name="salaire" value={formData.salaire} onChange={handleInputChange}
                                           className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                </div>
                            )}

                            {modalType === 'editPrime' && (
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-2">Nouvelle prime</label>
                                    <input type="number" name="prime" value={formData.prime} onChange={handleInputChange}
                                           className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                </div>
                            )}

                            {modalType === 'editProject' && (
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-2">Nouveau projet</label>
                                    <select name="idProjet" value={formData.idProjet} onChange={handleInputChange}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                                        <option value="">Non assigné</option>
                                        {projects.map(project => (
                                            <option key={project.id} value={project.id}>{project.nom_projet}</option>
                                        ))}
                                    </select>
                                </div>
                            )}

                            {modalType === 'addProject' && (
                                <div className="space-y-4">
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Nom du projet</label>
                                        <input type="text" name="nom_projet" value={formData.nom_projet} onChange={handleInputChange}
                                               className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Date début</label>
                                        <input type="date" name="dateDebut" value={formData.dateDebut} onChange={handleInputChange}
                                               className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Date fin</label>
                                        <input type="date" name="dateFin" value={formData.dateFin} onChange={handleInputChange}
                                               className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                                    </div>
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Statut</label>
                                        <select name="statut" value={formData.statut} onChange={handleInputChange}
                                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                                            <option value="En cours">En cours</option>
                                            <option value="Terminé">Terminé</option>
                                        </select>
                                    </div>
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
                                    onClick={() => {
                                        if (modalType === 'addProg') return handleAddProgrammer();
                                        if (modalType === 'editSalary') return handleUpdateSalary();
                                        if (modalType === 'editPrime') return handleUpdatePrime();
                                        if (modalType === 'editProject') return handleUpdateProject();
                                        if (modalType === 'addProject') return handleAddProject();
                                    }}
                                    className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-semibold"
                                >
                                    Valider
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