import { defHttp } from '/@/utils/http/axios';

enum Api {
  overview = '/campusqa/stats/overview',
  topQuestions = '/campusqa/stats/topQuestions',
  intentStats = '/campusqa/stats/intentStats',
}

export const getStatsOverview = () => defHttp.get({ url: Api.overview });

export const getTopQuestions = (params = { limit: 10 }) => defHttp.get({ url: Api.topQuestions, params });

export const getIntentStats = (params = { days: 30 }) => defHttp.get({ url: Api.intentStats, params });
