import { Module } from '@nestjs/common';
import { HelloController } from './hello.controller';
import { MetricsController } from './metrics.controller';

@Module({
  imports: [],
  controllers: [HelloController, MetricsController],
  providers: [],
})
export class AppModule {}
