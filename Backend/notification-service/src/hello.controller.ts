import { Controller, Get } from '@nestjs/common';
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';

@ApiTags('Notification Service Hello')
@Controller('api/notification')
export class HelloController {
  @Get('hello')
  @ApiOperation({
    summary: 'Hello notification-service (NestJS)',
    description:
      'Retourne un message de bienvenue, le nom du service NestJS, son statut et son horodatage.',
  })
  @ApiResponse({ status: 200, description: 'Réponse retournée avec succès' })
  sayHello() {
    return {
      message: 'Hello from notification-service!',
      service: 'notification-service',
      technology: 'Node.js / NestJS',
      status: 'UP',
      timestamp: new Date().toISOString(),
    };
  }
}
