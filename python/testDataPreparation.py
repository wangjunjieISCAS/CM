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
from vectorTransfer import VectorTransfer


doc2vec = Document2Vec()   

word2vec_train_file = 'data/word2vecTrain/train.txt'
word2vec_model_file = 'data/word2vecTrain/word2vec.bin'

project = 'zhudou'
#word_vector_input_file = 'data/testText/testTextWord-dixintong.txt'
#word_vector_input_file = 'data/testText/testTextWord-huayou.txt'
#word_vector_input_file = 'data/testText/testTextWord-niaoren.txt'
#word_vector_input_file = 'data/testText/testTextWord-nuomi.txt'
#word_vector_input_file = 'data/testText/testTextWord-weather.txt'
#word_vector_input_file = 'data/testText/testTextWord-weishi.txt'
#word_vector_input_file = 'data/testText/testTextWord-yun.txt'
word_vector_input_file = 'data/testText/testTextWord-' + project + '.txt'


word_embedding_out_file = 'data/testText/testTextInput.txt'
word_embedding_zero_line_file = 'data/testText/zeroLenListTest.npy'

#word_embedding_total_out_file = 'data/testText/testTextInputTotal-dixintong.txt'
#word_embedding_total_out_file = 'data/testText/testTextInputTotal-huayou.txt'
#word_embedding_total_out_file = 'data/testText/testTextInputTotal-niaoren.txt'
#word_embedding_total_out_file = 'data/testText/testTextInputTotal-nuomi.txt'
#word_embedding_total_out_file = 'data/testText/testTextInputTotal-weather.txt'
#word_embedding_total_out_file = 'data/testText/testTextInputTotal-weishi.txt'
#word_embedding_total_out_file = 'data/testText/testTextInputTotal-yun.txt'
word_embedding_total_out_file = 'data/testText/testTextInputTotal-' + project + '.txt'


embedding_size = 100

zero_len_list = doc2vec.predict_text2vec_obtain_text_source( embedding_size, word2vec_train_file, word2vec_model_file, word_vector_input_file, word_embedding_out_file, word_embedding_zero_line_file )

vectorTool = VectorTransfer()
vectorTool.transfer_vector( embedding_size, word_embedding_out_file, word_embedding_zero_line_file, word_embedding_total_out_file )



