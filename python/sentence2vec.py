# -*- coding: utf-8 -*-
"""
Created on Tue Jul 25 17:10:16 2017

@author: lenovo
"""
import numpy as np
from sklearn.decomposition import PCA

class Sentence2Vec(object):
    
    def get_word_frequency(self, word_index, sentence):
        return sentence.word_frequency[word_index]


# A SIMPLE BUT TOUGH TO BEAT BASELINE FOR SENTENCE EMBEDDINGS
# Sanjeev Arora, Yingyu Liang, Tengyu Ma
# Princeton University
# convert a list of sentence with word2vec items into a set of sentence vectors
    def sentence_to_vec(self, sentence_list, embedding_size, a=1e-3):
        zero_len_list = []
        sentence_set = []
        index = 0
        for sentence in sentence_list:
            vs = np.zeros(embedding_size)  # add all word2vec values into one vector for the sentence
            sentence_length = sentence.len()
            word_index =0;
            for word in sentence.word_list:
                a_value = a / (a + self.get_word_frequency(word_index, sentence ))  # smooth inverse frequency, SIF
                vs = np.add(vs, np.multiply(a_value, word.vector))  # vs += sif * word_vector
                word_index = word_index + 1;
             
            if sentence_length == 0:
                zero_len_list.append (index )
                #print "sentence_length: "
                #print sentence_length
            else:
                vs = np.divide(vs, sentence_length)  # weighted average
                sentence_set.append(vs)  # add to our existing re-calculated set of sentences
            index = index +1
        
        print zero_len_list
        # calculate PCA of this sentence set
        pca = PCA(n_components=embedding_size)
        pca.fit(np.array(sentence_set))
        u = pca.components_[0]  # the PCA vector
        u = np.multiply(u, np.transpose(u))  # u x uT

    # pad the vector?  (occurs if we have less sentences than embeddings_size)
        if len(u) < embedding_size:
            for i in range(embedding_size - len(u)):
                u = np.append(u, 0)  # add needed extension for multiplication below

    # resulting sentence vectors, vs = vs -u x uT x vs
        sentence_vecs = []
        for vs in sentence_set:
            sub = np.multiply(u,vs)
            sentence_vecs.append(np.subtract(vs, sub))
            print ( vs )
            
        return sentence_vecs, zero_len_list
