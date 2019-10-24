# -*- coding: utf-8 -*-
"""
Created on Wed Aug 30 15:29:23 2017

@author: lenovo
"""

import numpy as np
 
# combine the testText file and zeroLenListTest together, use the default vector to represent the null vector 
class VectorTransfer:
    def transfer_vector (self, embedding_size, word_embedding_out_file, word_embedding_zero_line_file, word_embedding_total_out_file ):
        text_vectors = []
       
        empty_vector = np.zeros(( embedding_size,) )
        print empty_vector.dtype
        
        zero_list = np.load( word_embedding_zero_line_file )
        print zero_list
        
        text = np.loadtxt( word_embedding_out_file )
        print text.dtype
        
        total_size = len(text) + len(zero_list)
        index_text = 0
        for i in range(total_size):
            if i in zero_list:
                text_vectors.append ( empty_vector )
                #print "null"
            else:
                text_vectors.append ( text[index_text] ) 
                
                index_text = index_text +1
        
        print text_vectors
        np.savetxt ( word_embedding_total_out_file, text_vectors)

#vectorTool = VectorTransfer()

#embedding_size = 100
#word_embedding_out_file = 'data/testText.txt'
#word_embedding_zero_line_file = 'data/zeroLenListTest.npy'

#word_embedding_total_out_file = 'data/testTextTotal.npy'

#vectorTool.transfer_vector( embedding_size , word_embedding_out_file, word_embedding_zero_line_file, word_embedding_total_out_file)

#array = np.load('data/testTextTotal.npy' )
#print array