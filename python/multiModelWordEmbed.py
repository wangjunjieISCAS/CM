# -*- coding: utf-8 -*-
"""
Created on Tue Jul 25 17:10:16 2017

@author: lenovo
"""
#load_ext autoreload
#autoreload 2
# ipython

import word2vec
import codecs
from sentence2vec import Sentence2Vec
import numpy as np
import sys

# generate the text train data for training the multi-model
class Word:
    def __init__(self, text, vector):
        self.text = text
        self.vector = vector


# a sentence, a list of words
class Sentence:
    def __init__(self, word_list, word_frequency):
        self.word_list = word_list
        self.word_frequency = word_frequency

    # return the length of a sentence
    def len(self):
        return len(self.word_list)
    
class Document2Vec:
    def train_model(self, embedding_size, word2vec_train_file, word2vec_model_file ):
        word2vec.word2vec( word2vec_train_file, word2vec_model_file, size=embedding_size, verbose=True)
        
    def get_model(self, word2vec_model_file ):
        return word2vec.load( word2vec_model_file )
  
    def predict_word2vec(self, word, model):
        if word in model.vocab:
            return model[word]
        else:
            return None
    
    def obtain_sentence_list (self, embedding_size, word2vec_train_file, word2vec_model_file, word_vector_input_file ):
        sentence_list = []
        
        #need to train the model, but do not need to train the model every time !!!!!!!!!!!!!!!!!!!!!!!!
        #self.train_model (embedding_size,  word2vec_train_file, word2vec_model_file  )     
        model = self.get_model( word2vec_model_file )
        #model.vocab.tofile('data/test.txt', sep=" ",format="%s")
        
        #termfile = codecs.open ( 'data/termList.txt', 'w', 'utf-8')
        #for term in model.vocab:
         #   termfile.write ( term )
          #  termfile.write ('\r\n')
        #termfile.close
        
        termfile = codecs.open ( 'data/termList.txt', 'r', 'utf-8')
        terms = termfile.readlines()
        termfile.close
        
        termlist = []
        vectorlist = []
        for line in terms:
            term = line.replace('\r\n', '')
            vector = self.predict_word2vec(term, model)
            termlist.append(term)
            vectorlist.append ( vector )
        
        
        embfile = codecs.open ('data/emblist.txt', 'w', 'gbk')
        for i in range(0, len(termlist)):
            #embfile.write ( termlist[i] )
            for value in vectorlist[i]:
                embfile.write ( value )
                embfile.write (' ' )
            embfile.write ('\r\n')
        embfile.close
                      
        #for term in model.vocab:
         #   termfile.write ( term )
          #  termfile.write ('\r\n')
        #termfile.close
        #print model.vocab
        #for term in model.vocab:
        #    print term

        file = codecs.open(word_vector_input_file,'r', 'utf-8')
        content = file.readlines()
        file.close()

        token_num = 0
        unfind_token_num = 0
        for line in content:
            line = line.replace('\r\n', '')
            #print line
            tokens = line.split(' ')
            
            # find the no duplicate word in the sentence, and 
            no_dup_tokens = []
            tf_tokens = []
            for word in tokens:
                #print "word -1 :" + word
                if word in no_dup_tokens:
                    index = no_dup_tokens.index(word )
                    tf_tokens[index] += 1
                else:
                    no_dup_tokens.append ( word)
                    tf_tokens.append (1)
            
            sentence_words = []
            for word in no_dup_tokens:
                #print "word: " + word
                #new_word = word.encode('utf-8', 'ignore').decode('utf-8')
                vector = self.predict_word2vec(word, model)
 
                if ( not (vector is None) ) :  
                    word_vector = Word( word, vector )
                    sentence_words.append (word_vector)
                    token_num += 1
                else:
                    unfind_token_num += 1
            
            sentence = Sentence (sentence_words, tf_tokens)
            sentence_list.append (sentence)
        
        print token_num
        print unfind_token_num
        
        return sentence_list
    
    def predict_sentence2vec(self, sentence_list, embedding_size):
        sen2vec = Sentence2Vec()
        sentence_vecs = sen2vec.sentence_to_vec(sentence_list, embedding_size)
        return sentence_vecs   
    
    def predict_text2vec_obtain_text_source (self, embedding_size, word2vec_train_file, word2vec_model_file, word_vector_input_file, word_embedding_out_file, word_embedding_zero_line_file):        
        sentence_list = self.obtain_sentence_list( embedding_size, word2vec_train_file, word2vec_model_file, word_vector_input_file )
        sentence_vecs, zero_len_list = self.predict_sentence2vec(sentence_list, embedding_size)
        np.savetxt( word_embedding_out_file, sentence_vecs)
        np.save( word_embedding_zero_line_file, zero_len_list)
        return zero_len_list

 
# generate the multi-model train file
#doc2vec = Document2Vec() 

#word2vec_train_file = 'data/word2vecTrain.txt'
#word2vec_model_file = 'data/word2vecTrain.bin'
#word_vector_input_file = 'data/inputText.txt'

#word_embedding_out_file = 'data/word2vecTrainOut.txt'
#word_embedding_zero_line_file = 'data/zeroLenListTrain.npy'

#embedding_size = 100

#zero_len_list = doc2vec.predict_text2vec_obtain_text_source( embedding_size, word2vec_train_file, word2vec_model_file, word_vector_input_file, word_embedding_out_file, word_embedding_zero_line_file )
#temp = np.load ( word_embedding_zero_line_file)
#print temp



