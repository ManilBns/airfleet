#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "list.h"

// Ajouter un avion en tête
Node* ajouterAvion(Node* head, Avion a) {
    Node* newNode = (Node*)malloc(sizeof(Node));
    newNode->data = a;
    newNode->next = head;
    return newNode;
}

// Supprimer par ID
Node* supprimerAvion(Node* head, int id) {
    Node* temp = head;
    Node* prev = NULL;

    while (temp != NULL) {
        if (temp->data.id == id) {
            if (prev == NULL) head = temp->next;
            else prev->next = temp->next;
            free(temp);
            return head;
        }
        prev = temp;
        temp = temp->next;
    }

    return head;
}

// Afficher tous les avions
void afficherListe(Node* head) {
    Node* current = head;
    while (current != NULL) {
        Avion a = current->data;
        printf("%d | %s %s | Cap: %d | Autonomie: %d | Crashs: %d | Service: %d\n",
               a.id, a.fabricant, a.modele, a.capacite, a.autonomie, a.crashs, a.anneeService);
        current = current->next;
    }
}

// Rechercher par modèle
Avion* rechercherParModele(Node* head, const char* modele) {
    Node* current = head;
    while (current != NULL) {
        if (strcmp(current->data.modele, modele) == 0)
            return &(current->data);
        current = current->next;
    }
    return NULL;
}
