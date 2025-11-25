#ifndef LIST_H
#define LIST_H

#include "avion.h"

typedef struct Node {
    Avion data;
    struct Node* next;
} Node;

// Fonctions
Node* ajouterAvion(Node* head, Avion a);
Node* supprimerAvion(Node* head, int id);
void afficherListe(Node* head);
Avion* rechercherParModele(Node* head, const char* modele);

#endif
