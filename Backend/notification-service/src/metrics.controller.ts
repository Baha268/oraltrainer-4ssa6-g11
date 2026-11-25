import { Controller, Get, Header } from '@nestjs/common';
import { ApiOperation, ApiTags } from '@nestjs/swagger';
import { collectDefaultMetrics, Registry } from 'prom-client';

const register = new Registry();
register.setDefaultLabels({ application: 'notification-service' });
collectDefaultMetrics({ register });

@ApiTags('Monitoring / Prometheus')
@Controller()
export class MetricsController {
  @Get('metrics')
  @Header('Content-Type', 'text/plain; version=0.0.4; charset=utf-8')
  @ApiOperation({ summary: 'Exposition des métriques Prometheus (/metrics)' })
  async getMetrics(): Promise<string> {
    return register.metrics();
  }

  @Get('actuator/prometheus')
  @Header('Content-Type', 'text/plain; version=0.0.4; charset=utf-8')
  @ApiOperation({ summary: 'Alias compatible Spring Boot Actuator (/actuator/prometheus)' })
  async getActuatorPrometheus(): Promise<string> {
    return register.metrics();
  }

  @Get('actuator/health')
  @ApiOperation({ summary: 'Health check compatible Actuator' })
  getHealth() {
    return { status: 'UP', service: 'notification-service' };
  }
}
