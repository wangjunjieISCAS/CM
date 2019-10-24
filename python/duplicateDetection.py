# -*- coding: utf-8 -*-
"""
Created on Mon Sep 18 20:34:49 2017

@author: wang
"""

import math


class DuplicationDetection:
    
    def compute_similarity_vectors ( self, vector1, vector2):
        sim = 0.0
        
        multiply = 0.0
        vec1_add = 0.0
        vec2_add = 0.0
        
        if ( len(vector1) != len(vector2) ):
            print ( "vector1 size is not equal with vector2 size!")
            return
        
        #print len(vector1)
        vector_len = len(vector1)
        if ( vector1[vector_len-1] == ' ' or vector1[vector_len-1] == '\n') :
            vector_len = len(vector1)-1
        
        for i in range(  vector_len ):
            vector1[i] = vector1[i].replace ( '\n' , "")
            if len(vector1[i]) == 0:
                continue;
            #print 'vector1 '  + "|" + vector1[i] + "|"
          
            value1 = float( vector1[i] )
            
            vector2[i] = vector2[i].replace ( '\n', "")
            value2 = float ( vector2[i] )
           
            multiply += value1 * value2
            
            vec1_add += value1 * value1
            vec2_add += value2 * value2
            
        add_multiply = vec1_add * vec2_add
        if ( add_multiply != 0.0):    
            add_multiply =  math.sqrt ( add_multiply)
            sim =  multiply /  add_multiply 
        
        #print sim
        # complex type, +0j
        return sim
    
    def load_report_vectors ( self, input_file):
        file = open( input_file ) 
        
        vector_dict = {}
        i = 0
        for line in file:
            temp = line.split ( " " ) 
            vector_dict[i] = temp
            i += 1
        
        #print vector_dict
        return vector_dict
    
    
    # for a report (index) in vector_dict, compute the similarity values with all other reports, sort them according to the similarity values
    def detect_duplicate_report ( self, vector_dict, index):
        sim_dict = {}
        
        test_vector = vector_dict[index]
        
        for i in range(len( vector_dict )):
            if ( i == index):
                continue;
            
            in_vector = vector_dict[i]
            sim = self.compute_similarity_vectors( test_vector, in_vector)
            sim_dict[i] = sim
        
        #print sim_dict
        
        dup_reports = []
        sorted_sim_dict = sorted(sim_dict.items(), key = lambda d:d[1], reverse=True )
        print sorted_sim_dict

        for key in sorted_sim_dict:
            dup_reports.append ( key[0] )
            
        #print dup_reports
        return dup_reports
    
    # the method to obtain the duplicate reports of all reports in a project, based on computing the similarity values
    def detect_duplicate_report_for_project ( self, vector_dict , output_file):
        with open( output_file, 'w') as f:
            for i in range( len(vector_dict) ) :
                f.write ( str(i) + ":" )
                
                dup_reports = self.detect_duplicate_report( vector_dict, i )
                for j in range( len(dup_reports) ):
                    f.write( str( dup_reports[j] ) + " ")
                f.write( "\n" )
        f.close


report_vector_file = "data/report_vector.txt"
predict_labels_file = "data/predict_labels.txt"

detection = DuplicationDetection()
#vector_dict = detection.load_report_vectors( report_vector_file )
#detection.detect_duplicate_report_for_project ( vector_dict, predict_labels_file)

#print dic
# ['a', 'b', 'c']
#print(sorted(dic, reverse=True))

            