<template>
  <div class="campusqa-stats">
    <a-card :bordered="false">
      <template #title>校园问答统计概览</template>
      <template #extra>
        <a-button type="primary" @click="loadData">刷新</a-button>
      </template>

      <a-spin :spinning="loading">
        <a-row :gutter="12">
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="metric-card">
              <div class="metric-label">知识库总数</div>
              <div class="metric-value">{{ overview.knowledgeTotal }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="metric-card">
              <div class="metric-label">启用知识数</div>
              <div class="metric-value">{{ overview.knowledgeEnabled }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="metric-card">
              <div class="metric-label">通知总数</div>
              <div class="metric-value">{{ overview.noticeTotal }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="metric-card">
              <div class="metric-label">已发布通知</div>
              <div class="metric-value">{{ overview.noticePublished }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="metric-card">
              <div class="metric-label">反馈总数</div>
              <div class="metric-value">{{ overview.feedbackTotal }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="metric-card">
              <div class="metric-label">已处理反馈</div>
              <div class="metric-value">{{ overview.feedbackHandled }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="metric-card">
              <div class="metric-label">反馈处理率</div>
              <div class="metric-value">{{ toPercent(overview.feedbackHandledRate) }}</div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="metric-card">
              <div class="metric-label">今日查询量</div>
              <div class="metric-value">{{ overview.historyToday }}</div>
            </div>
          </a-col>
        </a-row>

        <a-row :gutter="12" class="table-row">
          <a-col :xs="24" :lg="14">
            <a-card title="热门问题 TOP10" size="small">
              <a-table :columns="topColumns" :data-source="topQuestions" :pagination="false" row-key="id" size="small" />
            </a-card>
          </a-col>
          <a-col :xs="24" :lg="10">
            <a-card title="意图识别分布（近30天）" size="small">
              <a-table :columns="intentColumns" :data-source="intentStats" :pagination="false" row-key="intentType" size="small" />
            </a-card>
          </a-col>
        </a-row>
      </a-spin>
    </a-card>
  </div>
</template>

<script lang="ts" name="campusqa-stats" setup>
  import { onMounted, reactive, ref } from 'vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getIntentStats, getStatsOverview, getTopQuestions } from './stats.api';

  interface StatsOverview {
    knowledgeTotal: number;
    knowledgeEnabled: number;
    noticeTotal: number;
    noticePublished: number;
    feedbackTotal: number;
    feedbackHandled: number;
    feedbackHandledRate: number;
    historyTotal: number;
    historyToday: number;
    askTotal: number;
  }

  const loading = ref(false);
  const { createMessage } = useMessage();
  const overview = reactive<StatsOverview>({
    knowledgeTotal: 0,
    knowledgeEnabled: 0,
    noticeTotal: 0,
    noticePublished: 0,
    feedbackTotal: 0,
    feedbackHandled: 0,
    feedbackHandledRate: 0,
    historyTotal: 0,
    historyToday: 0,
    askTotal: 0,
  });
  const topQuestions = ref<Record<string, any>[]>([]);
  const intentStats = ref<Record<string, any>[]>([]);

  const topColumns = [
    {
      title: '问题',
      dataIndex: 'question',
      ellipsis: true,
    },
    {
      title: '点击量',
      dataIndex: 'hits',
      width: 90,
    },
    {
      title: '热门',
      dataIndex: 'hotFlag',
      width: 70,
      customRender: ({ text }) => (Number(text) === 1 ? '是' : '否'),
    },
  ];

  const intentColumns = [
    {
      title: '意图',
      dataIndex: 'intentLabel',
    },
    {
      title: '次数',
      dataIndex: 'count',
      width: 80,
    },
  ];

  function toPercent(value: number) {
    return `${Math.round(Number(value || 0) * 10000) / 100}%`;
  }

  function resolveResult<T>(payload: any, fallback: T): T {
    if (payload === null || payload === undefined) {
      return fallback;
    }
    if (typeof payload === 'object' && 'result' in payload) {
      return (payload.result ?? fallback) as T;
    }
    return payload as T;
  }

  async function loadData() {
    loading.value = true;
    try {
      const [overviewRes, topRes, intentRes] = await Promise.allSettled([
        getStatsOverview(),
        getTopQuestions({ limit: 10 }),
        getIntentStats({ days: 30 }),
      ]);

      const failedApis: string[] = [];
      if (overviewRes.status === 'fulfilled') {
        Object.assign(overview, resolveResult<Record<string, any>>(overviewRes.value, {}));
      } else {
        failedApis.push('概览');
      }

      if (topRes.status === 'fulfilled') {
        topQuestions.value = resolveResult<Record<string, any>[]>(topRes.value, []);
      } else {
        topQuestions.value = [];
        failedApis.push('热门问题');
      }

      if (intentRes.status === 'fulfilled') {
        intentStats.value = resolveResult<Record<string, any>[]>(intentRes.value, []);
      } else {
        intentStats.value = [];
        failedApis.push('意图统计');
      }

      if (failedApis.length > 0) {
        createMessage.error(`统计接口异常：${failedApis.join('、')}加载失败`);
      }
    } finally {
      loading.value = false;
    }
  }

  onMounted(() => {
    loadData();
  });
</script>

<style lang="less" scoped>
  .metric-card {
    border: 1px solid #f0f0f0;
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 12px;
    background: #fafafa;
  }

  .metric-label {
    color: #666;
    font-size: 13px;
  }

  .metric-value {
    margin-top: 6px;
    font-size: 24px;
    line-height: 1;
    font-weight: 600;
    color: #111;
  }

  .table-row {
    margin-top: 4px;
  }
</style>
