import request from 'supertest';
import express from 'express';
import dotenv from 'dotenv';
import jwt from 'jsonwebtoken';
import * as http from "http";
import nock from 'nock';

dotenv.config({ path: '.env.test' });

import app from './app';

describe('API Gateway Tests', () => {
  let server: http.Server;
  const PORT = 9999;
  
  const createToken = (userType: string, authorities: Array<{authority: string}>) => {
    const user = { 
      user: { 
        authorities, 
        id: 1, 
        name: `${userType} User`, 
        type: userType.toLowerCase(), 
        email: `${userType.toLowerCase()}@test.com` 
      } 
    };
    
    return jwt.sign(user, process.env.JWT_SECRET_KEY || 'teste123', {
      issuer: process.env.JWT_ISSUER || 'user-api',
      expiresIn: '1h'
    });
  };
  
  beforeAll((done) => {
    mockdatamanagementService();
    
    server = app.listen(PORT, () => {
      console.log(`Test server running on http://localhost:${PORT}`);
      done();
    });
  });

  afterAll((done) => {
    server.close(done);
    nock.cleanAll();
  });
  
  afterEach(() => {
    nock.cleanAll();
    mockdatamanagementService();

  });
  
  function mockdatamanagementService() {
    nock(process.env.USER_MANAGEMENT_API || 'http://datamanagement:8080')
      .get('/list')
      .reply(200, { message: 'Lista de usuários', users: [] })
      .post('/createUser')
      .reply(201, { message: 'Usuario cadastrado', userName: 'New User' })
      .put('/edit/1')
      .reply(200, { message: 'User updated', userName: 'Updated User' })
      .delete('/1')
      .reply(204)
      .get('/detail/1')
      .reply(200, { message: 'User details', user: { id: 1, name: 'Test User' } });
      
    nock(process.env.USER_MANAGEMENT_API || 'http://datamanagement:8080')
      .post('/auth/login', { email: 'admin@test.com', password: 'password123' })
      .reply(200, { message: 'Login realizado com sucesso', token: 'valid-token-admin' })
      .post('/auth/login', { email: 'invalid@test.com', password: 'wrongpassword' })
      .reply(401, { message: 'Email ou senha estao incorretos. Tente novamente.' })
      .post('/auth/login', { email: 'disabled@test.com', password: 'password123' })
      .reply(403, { message: 'Conta desativada' });
  }
});