/*
 * database.c
 *
 *  Created on: Nov 29, 2019
 *      Author: erenozturk
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAXLEN 20
#define false 0
#define true !false

// Structure Templates

typedef struct SR {				// The student record object
    char Last[MAXLEN];
	char First[MAXLEN];
	int ID;
	int marks;
} SRecord;

typedef struct bN {
	struct SR *Srec;
	struct bN *left;
	struct bN *right;
} bNode;

// Function Prototypes

bNode *addNode_Name(bNode *root, SRecord *Record);
bNode *addNode_ID(bNode *root, SRecord *Record);
bNode *makeNode(SRecord *data);

void inorder(bNode *root);
void search_Name(bNode *root, char *data);
void search_ID(bNode *root, int ID);
void str2upper(char *string);
void help();


bNode *match;


// Main entry point is here.  Program uses the standard Command Line Interface

int main(int argc, char *argv[]) {

// Internal declarations

    FILE * NAMESIDS;        // File descriptor (an object)!
	FILE * MARKS;           // Will have two files open

    bNode *root_N;   		// Pointer to names B-Tree
    bNode *root_I;   		// Pointer to IDs B-Tree
    SRecord *Record;   		// Pointer to current record read in

	int NumRecords;
	char cmd[MAXLEN], sName[MAXLEN];
	int sID;

// Argument check
        if (argc != 3) {
                printf("Usage: sdb [Names+IDs] [marks] \n");
                return -1;
        }

// Attempt to open the user-specified file.  If no file with
// the supplied name is found, exit the program with an error
// message.

        if ((NAMESIDS=fopen(argv[1],"r"))==NULL) {
                printf("Can't read from file %s\n",argv[1]);
                return -2;
        }

        if ((MARKS=fopen(argv[2],"r"))==NULL) {
                printf("Can't read from file %s\n",argv[2]);
                fclose(NAMESIDS);
                return -2;
        }

// Initialize B-Trees by creating the root pointers;

    root_N=NULL;
	root_I=NULL;

//  Read through the NamesIDs and marks files, record by record.

	NumRecords=0;

	printf("Building database...\n");

	while(true) {

// 	Allocate an object to hold the current data

		Record = (SRecord *)malloc(sizeof(SRecord));
		if (Record == NULL) {
			printf("Failed to allocate object for data in main\n");
			return -1;
		}

//  Read in the data.  If the files are not the same length, the shortest one
//  terminates reading.

		int status = fscanf(NAMESIDS,"%s%s%d",Record->First,Record->Last,&Record->ID);
		if (status == EOF) break;
		status = fscanf(MARKS,"%d",&Record->marks);
		if (status == EOF) break;
		NumRecords++;


//	Add the record just read in to 2 B-Trees - one ordered
//  by name and the other ordered by student ID.

	    root_N=addNode_Name(root_N,Record);
	    root_I=addNode_ID(root_I,Record);


	}

// Close files once we're done

	fclose(NAMESIDS);
	fclose(MARKS);

	printf("Finished, %d records found...\n",NumRecords);


//
//  Simple Command Interpreter
//

	while (1) {
	    printf("sdb> ");
	    scanf("%s",cmd);					  // read command
	    str2upper(cmd);

// List by Name

		if (strncmp(cmd,"LN",2)==0) {         // List all records sorted by name
			printf("Student Record Database sorted by Last Name\n\n");
			inorder(root_N);
			printf("\n");
		}

// List by ID

		else if (strncmp(cmd,"LI",2)==0) {    // List all records sorted by ID
			printf("Student Record Database sorted by Student ID\n\n");
			inorder(root_I);
			printf("\n");
		}

// Find record that matches Name

		else if (strncmp(cmd,"FN",2)==0) {    // List record that matches name
			printf("Enter name to search: ");
			scanf("%s",sName);
			match=NULL;
			search_Name(root_N,sName);
			if (match==NULL)
			  printf("There is no student with that name.\n");
	        else {
			  if (strlen(match->Srec->First)+strlen(match->Srec->Last)>15) {
				printf("\nStudent Name:\t%s %s\n",match->Srec->First,match->Srec->Last);
			  } else {
				printf("\nStudent Name:\t\t%s %s\n",match->Srec->First,match->Srec->Last);
			  }
			  printf("Student ID:\t\t%d\n",match->Srec->ID);
			  printf("Total Grade:\t\t%d\n\n",match->Srec->marks);
	        }
		}

// Find record that matches ID


		else if (strncmp(cmd,"FI",2)==0) {    // List record that matches ID
			printf("Enter ID to search: ");
			scanf("%d",&sID);
			match=NULL;
			search_ID(root_I,sID);
			if (match==NULL)
			  printf("There is no student with that ID.\n");
	        else {
			  if (strlen(match->Srec->First)+strlen(match->Srec->Last)>15) {
				printf("\nStudent Name:\t%s %s\n",match->Srec->First,match->Srec->Last);
			  } else {
				printf("\nStudent Name:\t\t%s %s\n",match->Srec->First,match->Srec->Last);
			  }
			printf("Student ID:\t\t%d\n",match->Srec->ID);
			printf("Total Grade:\t\t%d\n\n",match->Srec->marks);
	      }
		}



// Help

		else if (strncmp(cmd,"H",1)==0) {  // Help
			help();
		}

		else if (strncmp(cmd,"?",2)==0) {     // Help
			help();
		}

// Quit

		else if (strncmp(cmd,"Q",1)==0) {  // Help
			printf("Program terminated...\n");
			return 0;
		}

// Command not understood

		else {
			printf("Command not understood.\n");
		}
	}

}

//
//	Functions described in prototypes:
//
bNode *makeNode(SRecord *data){    //Makes a node with data provided as an SRecord
	bNode *node = (bNode*)malloc(sizeof(bNode));
	node->left = NULL;
	node->right = NULL;
	node->Srec = data;
	return node;
}

//Traverse a bTree from a root, print out every node's Srec in the correct format.
//Used in sorting the bTree's made from names and ID's.

void inorder(bNode *root){
	if(root->left != NULL) inorder(root->left);
	printf("%-12s %-12s %-5d \t %-3d \n",root->Srec->First,root->Srec->Last,root->Srec->ID,root->Srec->marks);
	if(root->right != NULL) inorder(root->right);
}

//Add a node with a specified Record to the bTree made from ID's, return
//bTree's root at the end

bNode *addNode_ID(bNode *root, SRecord *Record){
	bNode *current;

	if(root==NULL){
		root = makeNode(Record);
		return root;
	}else{
		current = root;
		while(true){
			if((Record->ID)<(current->Srec->ID)){
				if(current->left == NULL){
					current->left = makeNode(Record);
					return root;
					break;
				}else{
					current = current->left;
				}
			}else{
				if(current->right == NULL){
					current->right = makeNode(Record);
					makeNode(Record);
					return root;
					break;
				}else{
					current = current->right;
				}
			}
		}
	}
	return root;
}

//Add a node with the specified SRecord to the bTree made from names,
//return its root at the end

bNode *addNode_Name(bNode *root, SRecord *Record){
	bNode *current;

	if(root==NULL){
		root = makeNode(Record);
		return root;
	}else{
		current = root;
		while(true){
			if(strcmp(Record->Last,current->Srec->Last)<0){
				if(current->left == NULL){
					current->left = makeNode(Record);
					return root;
					break;
				}else{
					current = current->left;
				}
			}else{
				if(current->right == NULL){
					current->right = makeNode(Record);
					makeNode(Record);
					return root;
					break;
				}else{
					current = current->right;
				}
			}
		}
	}
	return root;
}
//Search the ID bTree for int ID. Print an error message if
//tree is not initialised. Break whenever it's impossible for an
//ID to be in the tree, returning control to the command interpreter.

void search_ID(bNode *root, int ID){
	bNode *current;
	if(root == NULL){
		printf("No database found!");
	}else{
		current = root;
		while(true){
			if(ID<(current->Srec->ID)){
				if(current->left != NULL){
					current = current->left;
				}else{
					break;
				}
			}else if(ID>(current->Srec->ID)){
				if(current->right != NULL){
					current = current->right;
				}else{
					break;
				}
			}else if(ID == (current->Srec->ID)){
				match = current;
				break;
			}
		}
	}
}

//Search the last name bTree for char *data. Print an error message if
//tree is not initialised. Break whenever it's impossible for a
//name to be in the tree, returning control to the command interpreter.

void search_Name(bNode *root, char *data){
	bNode *current;
	if(root == NULL){
		printf("No database found!");
	}else{
		current = root;
		while(true){
			if(strcasecmp(data,current->Srec->Last)<0){
				if(current->left != NULL){
					current = current->left;
				}else{
					break;
				}
			}else if(strcasecmp(data,current->Srec->Last)>0){
				if(current->right != NULL){
					current = current->right;
				}else{
					break;
				}
			}else{
				if(strcasecmp(data,current->Srec->Last) == 0){
				match = current;
				break;
			}
		}
	}
  }
}



//
//  Convert a string to upper case
//

void str2upper (char *string) {
    int i;
    for(i=0;i<strlen(string);i++)
       string[i]=toupper(string[i]);
     return;
}

// Help
// prints command list

void help() {
	printf("LN List all the records in the database ordered by last name.\n");
	printf("LI List all the records in the database ordered by student ID.\n");
	printf("FN Prompts for a name and lists the record of the student with the corresponding name.\n");
	printf("FI Prompts for a name and lists the record of the student with the Corresponding ID.\n");
	printf("HELP Prints this list.\n");
	printf("? Prints this list.\n");
	printf("Q Exits the program.\n\n");

	return;
}
