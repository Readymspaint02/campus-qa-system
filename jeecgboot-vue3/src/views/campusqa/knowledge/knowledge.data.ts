import { BasicColumn, FormSchema } from '/@/components/Table';

import { render } from '/@/utils/common/renderUtils';

const statusOptions = [
  { label: '启用', value: 'enable' },
  { label: '停用', value: 'disable' },
];

const categoryDictCode = 'campus_qa_category,name,id';

const statusMap: Record<string, string> = {
  enable: '启用',
  disable: '停用',
};

export const columns: BasicColumn[] = [
  {
    title: '问题',
    dataIndex: 'question',
    align: 'left',
  },
  {
    title: '分类',
    dataIndex: 'categoryId',
    width: 160,
    customRender: ({ text }) => render.renderDict(text, categoryDictCode),
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 120,
    customRender: ({ text }) => statusMap[String(text)] || text,
  },
  {
    title: '热门',
    dataIndex: 'hotFlag',
    width: 80,
    customRender: ({ text }) => (Number(text) === 1 ? '是' : '否'),
  },
  {
    title: '点击量',
    dataIndex: 'hits',
    width: 80,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 180,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'question',
    label: '问题',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'categoryId',
    label: '分类',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: categoryDictCode,
      placeholder: '请选择分类',
    },
    colProps: { span: 8 },
  },
  {
    field: 'status',
    label: '状态',
    component: 'Select',
    componentProps: {
      options: statusOptions,
    },
    colProps: { span: 8 },
  },
];

export const formSchema: FormSchema[] = [
  {
    label: '编号',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    field: 'categoryId',
    label: '分类',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: categoryDictCode,
      placeholder: '请选择分类',
    },
    required: true,
  },
  {
    field: 'question',
    label: '问题',
    component: 'Input',
    required: true,
  },
  {
    field: 'answer',
    label: '答案',
    component: 'InputTextArea',
    required: true,
  },
  {
    field: 'keywords',
    label: '关键词',
    component: 'Input',
  },
  {
    field: 'tags',
    label: '标签',
    component: 'Input',
  },
  {
    field: 'hotFlag',
    label: '热门',
    component: 'Switch',
    componentProps: {
      checkedValue: 1,
      unCheckedValue: 0,
    },
  },
  {
    field: 'hits',
    label: '点击量',
    component: 'InputNumber',
    componentProps: {
      min: 0,
    },
  },
  {
    field: 'status',
    label: '状态',
    component: 'Select',
    componentProps: {
      options: statusOptions,
    },
  },
  {
    field: 'source',
    label: '来源',
    component: 'Input',
  },
];
