#include <stdio.h>
#include <stdlib.h>
#include "algorithms.h"

// --------------------------------------------------
// Fonction d'échange pour le tri
// --------------------------------------------------
void swapAvion(Avion* a, Avion* b) {
    Avion temp = *a;
    *a = *b;
    *b = temp;
}

// --------------------------------------------------
// Tri par nombre de crashs (ascendant)
// --------------------------------------------------
Node* trierParCrashs(Node* head) {
    if (!head) return NULL;

    Node* i;
    Node* j;
    for (i = head; i->next != NULL; i = i->next) {
        for (j = i->next; j != NULL; j = j->next) {
            if (i->data.crashs > j->data.crashs) {
                swapAvion(&(i->data), &(j->data));
            }
        }
    }
    return head;
}

// --------------------------------------------------
// Tri par autonomie (descendant)
// --------------------------------------------------
Node* trierParAutonomie(Node* head) {
    if (!head) return NULL;

    Node* i;
    Node* j;
    for (i = head; i->next != NULL; i = i->next) {
        for (j = i->next; j != NULL; j = j->next) {
            if (i->data.autonomie < j->data.autonomie) {
                swapAvion(&(i->data), &(j->data));
            }
        }
    }
    return head;
}

// --------------------------------------------------
// Moyenne des crashs
// --------------------------------------------------
double moyenneCrashs(Node* head) {
    if (!head) return 0.0;
    int sum = 0, count = 0;
    Node* current = head;
    while (current) {
        sum += current->data.crashs;
        count++;
        current = current->next;
    }
    return (double)sum / count;
}

// --------------------------------------------------
// Moyenne autonomie
// --------------------------------------------------
double moyenneAutonomie(Node* head) {
    if (!head) return 0.0;
    int sum = 0, count = 0;
    Node* current = head;
    while (current) {
        sum += current->data.autonomie;
        count++;
        current = current->next;
    }
    return (double)sum / count;
}

// --------------------------------------------------
// Maximum crashs
// --------------------------------------------------
int maxCrashs(Node* head) {
    if (!head) return 0;
    int max = head->data.crashs;
    Node* current = head->next;
    while (current) {
        if (current->data.crashs > max)
            max = current->data.crashs;
        current = current->next;
    }
    return max;
}

// --------------------------------------------------
// Minimum crashs
// --------------------------------------------------
int minCrashs(Node* head) {
    if (!head) return 0;
    int min = head->data.crashs;
    Node* current = head->next;
    while (current) {
        if (current->data.crashs < min)
            min = current->data.crashs;
        current = current->next;
    }
    return min;
}
