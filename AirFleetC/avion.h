#ifndef AVION_H
#define AVION_H

#define MAX_STRING 50

typedef struct {
    int id;
    char fabricant[50];
    char modele[50];
    int capacite;
    int autonomie;
    int crashs;
    int anneeService;
} Avion;

#endif
