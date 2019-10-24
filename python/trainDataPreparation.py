# -*- coding: utf-8 -*-
"""
Created on Tue Jul 25 17:10:16 2017

@author: lenovo
"""
#load_ext autoreload
#autoreload 2
# ipython

import numpy as np
from multiModelWordEmbed import *
from vectorTransfer import *

doc2vec = Document2Vec() 

word2vec_train_file = 'data/word2vecTrain.txt'
word2vec_model_file = 'data/word2vecTrain.bin'
word_vector_input_file = 'data/inputText.txt'

word_embedding_out_file = 'data/word2vecTrainOut.txt'
word_embedding_zero_line_file = 'data/zeroLenListTrain.npy'

word_embedding_total_out_file = 'data/outputTextEmbeding.txt'

embedding_size = 100

zero_len_list = doc2vec.predict_text2vec_obtain_text_source( embedding_size, word2vec_train_file, word2vec_model_file, word_vector_input_file, word_embedding_out_file, word_embedding_zero_line_file )

#vectorTool = VectorTransfer()
#vectorTool.transfer_vector( embedding_size, word_embedding_out_file, word_embedding_zero_line_file, word_embedding_total_out_file )

#temp = np.load ( word_embedding_zero_line_file)
#print temp