#include <stdio.h>
#include "list.h"

int main() {
    Node* head = NULL;

    Avion a1 = {1, "Airbus", "A320", 180, 6100, 30, 1988};
    Avion a2 = {2, "Boeing", "737-800", 189, 5420, 70, 1998};

    head = ajouterAvion(head, a1);
    head = ajouterAvion(head, a2);

    printf("Liste des avions :\n");
    afficherListe(head);

    printf("\nRecherche du modèle A320 :\n");
    Avion* res = rechercherParModele(head, "A320");
    if (res != NULL) printf("Trouvé : %s %s\n", res->fabricant, res->modele);
    else printf("Non trouvé\n");

    head = supprimerAvion(head, 1);
    printf("\nAprès suppression ID=1 :\n");
    afficherListe(head);

    return 0;
}
