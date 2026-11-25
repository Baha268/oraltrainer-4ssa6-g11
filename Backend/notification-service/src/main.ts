import 'reflect-metadata';
import { NestFactory } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { Eureka } from 'eureka-js-client';
import { AppModule } from './app.module';

async function bootstrap() {
  const port = Number(process.env.PORT ?? 8088);
  const app = await NestFactory.create(AppModule);

  const config = new DocumentBuilder()
    .setTitle('OralTrain - Notification Service API')
    .setDescription(
      "Service commun (Node.js / NestJS, MongoDB) sans logique métier : écoute les événements RabbitMQ (exchange oral.events), " +
        "gère l'idempotence (EvenementTraite), applique les modèles et préférences utilisateur, et diffuse les notifications in-app (WebSocket) et e-mail.",
    )
    .setVersion('2.0.0')
    .setContact(
      'OralTrain Team (4SSA6-G1)',
      'https://github.com/Baha268/oraltrainer-4ssa6-g11',
      'contact@oraltrain.local',
    )
    .addServer(`http://localhost:${port}`, 'Serveur local - notification-service (NestJS)')
    .build();

  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('swagger-ui/index.html', app, document, {
    jsonDocumentUrl: 'v3/api-docs',
  });
  SwaggerModule.setup('swagger-ui', app, document);

  await app.listen(port);

  if (process.env.EUREKA_ENABLED !== 'false') {
    const eurekaClient = new Eureka({
      instance: {
        app: 'NOTIFICATION-SERVICE',
        instanceId: `localhost:notification-service:${port}`,
        hostName: '127.0.0.1',
        ipAddr: '127.0.0.1',
        statusPageUrl: `http://127.0.0.1:${port}/api/notification/hello`,
        port: { $: port, '@enabled': true },
        vipAddress: 'notification-service',
        dataCenterInfo: {
          '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
          name: 'MyOwn',
        },
      },
      eureka: {
        host: process.env.EUREKA_HOST ?? 'localhost',
        port: Number(process.env.EUREKA_PORT ?? 8761),
        servicePath: '/eureka/apps/',
        maxRetries: 3,
      },
    });
    eurekaClient.start((err) => {
      if (err) {
        console.warn('Eureka registration warning:', err.message ?? err);
      }
    });
  }
}

bootstrap();
