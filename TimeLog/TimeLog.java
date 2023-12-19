import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TimeLog {

    List<Employe> listeEmployes = new ArrayList<>();
    List<Projet> listeProjets = new ArrayList<>();
    Rapport rapport = new Rapport();
    private Lecture lecture;
    private Ecriture ecriture;
    

    /**
     * Constructeur de classe
     */
    public TimeLog(){
        this.listeEmployes = new ArrayList<>();
        this.listeProjets = new ArrayList<>();
        this.lecture = new Lecture();
        this.ecriture = new Ecriture();
        // Charger les données existantes depuis le fichier JSON au démarrage du TimeLog
        this.chargerDonnees();
    }

    /*********************************************************************************************************
     **************************************** ACCESSEURS & MODIFICATEURS *************************************
     *********************************************************************************************************/

    public List<Employe> getListeEmployes(){
        return listeEmployes;
    }

    public List<Projet> getListeProjets(){
        return listeProjets;
    }

    public void setListeEmployes(ArrayList<Employe> newListeEmployes){
        this.listeEmployes = newListeEmployes;
        sauvegarderDonnees();
    }

    public void setListeProjets(ArrayList<Projet> newListeProjets){
        this.listeProjets = newListeProjets;
        sauvegarderDonnees();
    }



    /**************************************** FONCTIONS DE CLASSE *******************************************
    *********************************************************************************************************/

    /**
     * Ajouter un employé à la liste d'employés.
     * 
     * @param employe L'employé à ajouter.
     */
    public void ajouterEmployer(Employe employe){
        if(listeEmployes.contains(employe)){        // Vérifie si l'employé est déjà présent dans la liste
            System.out.println("Employer existant dans le systeme.");
        }else{                                      // Ajoute l'employé à la liste
            listeEmployes.add(employe);
            sauvegarderDonnees();                   // Sauvegarder après chaque modification
        }
    }

    /**
     * Ajouter un projet à la liste des projets.
     * 
     * @param projet Le projet à ajouter
     */
    public void ajouterProjet(Projet projet){
        if(listeProjets.contains(projet)){          // Vérifie si le projet est déjà présent dans la liste
            System.out.println("Projet existant dans le systeme.");
        }else{                                      // Ajoute le projet à la liste
            listeProjets.add(projet);
            sauvegarderDonnees();                   // Sauvegarder après chaque modification
        }
    }

    /**
    * Démarre une activité pour un employé basé sur l'ID utilisateur donné.
    *
    * @param idUtilisateur L'identifiant de l'utilisateur (employé) qui démarre l'activité.
    *
    * Ce méthode effectue les étapes suivantes :
    * 1. Demande le nom du projet et la discipline via l'interface de menu.
    * 2. Récupère les informations de l'employé courant en utilisant son ID.
    * 3. Tente de récupérer le projet actuel en utilisant le nom du projet.
    * 4. Si le projet existe :
    *    a. Vérifie si l'employé courant a déjà une activité en cours.
    *    b. Si aucune activité n'est en cours pour cet employé :
    *       i. Crée une nouvelle activité avec l'ID de l'utilisateur et les informations de l'employé.
    *       ii. Signale le début de l'activité avec le nom du projet et la discipline.
    *       iii. Ajoute la nouvelle activité au projet courant.
    *       iv. Affiche un message confirmant le début de l'activité.
    *       v. Met à jour l'activité courante de l'employé avec la nouvelle activité.
    *    c. Si une activité est déjà en cours pour cet employé, affiche un message indiquant qu'une activité est déjà en cours.
    * 5. Si le projet n'existe pas, affiche un message indiquant que le projet n'existe pas.
    */
    public void traitementDemarrage(int idUtilisateur){

        String nomProjet = InterfaceMenu.demanderNomProjet();
        String discipline = InterfaceMenu.demanderDiscipline();
        Employe employeCourant = obtenirEmploye(idUtilisateur);
        Projet projetCourant = obtenirProjet(nomProjet);

        if(projetCourant != null){
            // Verifier si activite deja 
            if(employeCourant.getActiviteCourant() == null){
                Activite nouvelleActivite = new Activite(employeCourant);
                nouvelleActivite.signalerDebutActivite(projetCourant, discipline);
                projetCourant.ajouterActivite(nouvelleActivite);
                System.out.println("Activite commencer --> "+nouvelleActivite.getIdActivite());
                employeCourant.setActiviteCourant(nouvelleActivite);
            }
            else{
                System.out.println("Activite deja en cours pour cette employe.");
            }
            
        }else{
            System.out.println("Projet non existant.");
        }
    }

    /**
    * Termine l'activité en cours pour un employé spécifié par son ID utilisateur.
    *
    * @param idUtilisateur L'identifiant de l'utilisateur (employé) dont l'activité doit être terminée.
    *
    * Cette méthode effectue les opérations suivantes :
    * 1. Récupère les informations de l'employé courant en utilisant son ID.
    * 2. Vérifie si l'employé courant a une activité en cours.
    * 3. Si aucune activité n'est en cours pour cet employé :
    *    a. Affiche un message indiquant qu'il n'y a aucune activité en cours.
    * 4. Si une activité est en cours :
    *    a. Signale la fin de l'activité en cours.
    *    b. Réinitialise l'activité courante de l'employé à null.
    *    c. Affiche un message confirmant la fin de l'activité en cours.
    */
    public void traitementTerminaison(int idUtilisateur){
        Employe employeCourant = obtenirEmploye(idUtilisateur);
        if(employeCourant.getActiviteCourant()==null){
            System.out.println("Aucune activite en cours.");
        }
        else{
            employeCourant.getActiviteCourant().signalerFinActivite();
            employeCourant.setActiviteCourant(null);
            System.out.println("Activite en cours terminer.");
        }
    }

    public void menuTimeLog(){
        interfaceMenu();
    }

    /**
    * Affiche le menu principal du programme et gère la navigation de l'utilisateur.
    * Cette méthode affiche un menu de console répétitif jusqu'à ce que l'utilisateur choisisse de quitter.
    * À chaque itération, elle invite l'utilisateur à faire un choix, traite ce choix via la méthode
    * {@link #traitementMenu(int)} et vérifie si l'utilisateur a choisi de quitter le programme.
    */
    public  void interfaceMenu(){
        System.out.println("-- Bienvenue sur le programme TimeLog --\n");
        int choixMenu = -1;
        while (choixMenu != 4) {  
            System.out.println("Veuillez faire un choix et entre le numero du menu (1-4): ");
            choixMenu = InterfaceMenu.choixMenu();
            traitementMenu(choixMenu);
        }
    }

    public  void traitementMenu(int choixMenu){
        if(choixMenu == 1){
            menuEmploye();
        }else if(choixMenu == 2){
            //menuAdmin();
            // TODO
        }else if(choixMenu == 3){
            // Rapport sans connection
        }else if(choixMenu == 4){
            // Quitter
            System.out.println("Au revoir !");
        }
        System.out.println("\n");
    }

    /**
     * Vérifie la connexion d'un employé en comparant l'identifiant et le nom fournis avec ceux de la liste des employés.
     * 
     * @param idEmploye L'identifiant de l'employé à vérifier.
     * @param nomEmploye Le nom de l'employé à vérifier.
     * @return vrai si l'identifiant et le nom correspondent à un employé dans la liste; faux sinon.
     */
    public boolean verifierConnexionEmploye(int idEmploye, String nomEmploye){
        for (Employe employe : listeEmployes) {
            if(idEmploye == employe.getIdEmploye() && nomEmploye.equals(employe.getNomEmploye()))
                return true;
        }
        return false;           // Par défaut, si aucun employé correspondant trouvé
    }

    public void menuEmploye(){
        int idUtilisateur = InterfaceMenu.demandeIDUtilisateur();
        String nomUtilisateur = InterfaceMenu.demandeNomUtilisateur();
        boolean connectionEtablie = verifierConnexionEmploye(idUtilisateur, nomUtilisateur);
        interfaceMenuEmployeConnecter(idUtilisateur,connectionEtablie);  
    }

    public void interfaceMenuEmployeConnecter(int idUtilisateur,boolean connectionEtablie){
        if(connectionEtablie){
            InterfaceMenu.menuEmployeConnecter();
            Scanner choixScan = new Scanner(System.in);
            System.out.print("Votre choix en entier: ");
            int choix = Integer.parseInt(choixScan.nextLine());
            if(choix == 1){
                traitementDemarrage(idUtilisateur);
            }else if(choix == 2){
                traitementTerminaison(idUtilisateur);
            }
            // Ajouter autre choix
            // TODO
            System.out.println("\n");
        }
    }

    public Employe obtenirEmploye(int idUtilisateur){
        for (Employe employe : listeEmployes) {
            if(employe.getIdEmploye() == idUtilisateur){
                return employe;
            }
        }
        return null;
    }

    public Projet obtenirProjet(String nomProjet){
        for (Projet projet : listeProjets) {
            if(projet.getNomProjet().equals(nomProjet)){
                return projet;
            }
        }
        return null;
    }

    public String demandeSalaire(int idUtilisateur){
        Date debut;
        Date fin; 
        ArrayList<Activite> list = trouverActivitesEmploye(idUtilisateur);
        
        return null;
    }

    public String obtenirSalaireDate(ArrayList<Activite> listeActivite){
        int  montant =0;
        for (Activite activite : listeActivite) {
            activite.calculerHeuresTravaillees();
        }

        return null;
    }

    public ArrayList trouverActivitesEmploye(int idUtilisateur){
        ArrayList listeActivites = new ArrayList<>();
        for (Projet projet : listeProjets) {
            for (Activite activite : projet.listeActivites) {
                if(activite.getEmploye().getID() == idUtilisateur){
                    listeActivites.add(activite);
                }
            }
        }
        return listeActivite;
    }

    /**************************************** FONCTIONS UTILITAIRES *****************************************
    *********************************************************************************************************/

    /**
     * Charger données Employe et Projet depuis le fichier JSON
     */
    private void chargerDonnees() {
        listeEmployes = new ArrayList<>(lecture.lireEmployes());    
        listeProjets = new ArrayList<>(lecture.lireProjets());      

    }

    /**
     * Sauvegarder les données Employe et Projet dans le fichier JSON.
     */
    private void sauvegarderDonnees() {
        ecriture.ecrireEmployes(listeEmployes);      
        ecriture.ecrireProjets(listeProjets);        
    }


}