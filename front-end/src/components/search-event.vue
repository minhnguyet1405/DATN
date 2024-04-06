<template>
  <span class="search-template">
    <el-popover
      v-model="visible"
      placement="bottom-start"
      title=""
      width="380"
      :visible-arrow="false"
      trigger="click"
    >
      <div class="search-form-content">
        <el-form ref="formSearch" :model="filter" :rules="rules">
          <el-form-item label="Thời gian phát hiện" prop="date">
            <el-date-picker
              v-model="filter.date"
              type="datetimerange"
              :clearable="false"
              range-separator="-"
              start-placeholder="Bắt đầu"
              format="dd/MM/yyyy HH:mm:ss"
              end-placeholder="Kết thúc"
            />
            <!-- :picker-options="datePickerOptions" -->
          </el-form-item>
          <el-form-item label="Loại sự kiện">
            <el-select
              v-model="filter.eventType"
              clearable
              placeholder="Chọn loại sự kiện"
            >
              <el-option
                v-for="type in eventTypeList"
                :key="type.value"
                :label="type.label"
                :value="type.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="Loại đối tượng">
            <el-select
              v-model="filter.objectType"
              multiple
              filterable
              clearable
              collapse-tags
              :class="{ multi: filter.objectType && filter.objectType.length > 1 }"
              class="site-search-popup"
              placeholder="Chọn loại đối tượng"
            >
              <el-option
                v-for="type in objectTypeList"
                :key="type.value"
                :label="type.label"
                :value="type.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="Nguồn phát hiện">
            <el-select v-model="filter.sourceType" clearable placeholder="Chọn nguồn phát hiện">
              <el-option
                v-for="type in sourceTypeList"
                :key="type.value"
                :label="type.label"
                :value="type.value"
              />
            </el-select>
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="24">
              <div class="grid-content bg-purple">
                <el-form-item label="Trạng thái">
                  <el-select
                    v-model="filter.status"
                    multiple
                    filterable
                    clearable
                    collapse-tags
                    :class="{ multi: filter.status && filter.status.length > 1 }"
                    class="site-search-popup"
                    placeholder="Chọn trạng thái"
                  >
                    <el-option
                      v-for="type in statusList"
                      :key="type.value"
                      :label="type.label"
                      :value="type.value"
                    />
                  </el-select>
                </el-form-item>
              </div>
            </el-col>
          </el-row>
          <el-form-item class="action">
            <el-button
              type="info"
              @click="refresh()"
            ><i class="el-icon-refresh" /> Làm mới</el-button>
            <el-button type="primary" @click="search()">Áp dụng</el-button>
          </el-form-item>
        </el-form>
      </div>
      <span slot="reference">
        <img :src="filter_icon" alt="">
      </span>
    </el-popover>
  </span>
</template>
<script type="text/javascript">
import filter_icon from '@/assets/images/filter.png'
import moment from 'moment'

export default {
  props: {
    searchEvent: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      filter_icon: filter_icon,
      visible: false,
      loading: false,
      eventHandMade: false,
      filter: {
        date: [
          moment().subtract(3, 'day').startOf('day').format('YYYY-MM-DD HH:mm:ss'),
          moment().endOf('day').format('YYYY-MM-DD HH:mm:ss')
        ],
        eventType: [],
        objectType: [],
        sourceType: [],
        status: []
      },
      rules: {
        date: [
          {
            required: true,
            message: 'Khoảng thời gian tìm kiếm là bắt buộc',
            trigger: 'blur'
          }
        ]
      },
      datePickerOptions: {
        disabledDate(date) {
          return date > moment().valueOf()
        }
      },
	  eventTypeList: [
        {
          value: 'IN',
          label: 'Vào'
        },
        {
          value: 'OUT',
          label: 'Ra'
        }
      ],
      objectTypeList: [
        {
          value: 'CAR',
          label: 'Ô tô'
        },
        {
          value: 'MOTO',
          label: 'Xe máy'
        },
        {
          value: 'TRAM',
          label: 'Xe đạp điện'
        },
        {
          value: 'BIKE',
          label: 'Xe đạp'
        }
      ],
      sourceTypeList: [
        {
          value: 'AUTO',
          label: 'Nguồn tự động'
        },
        {
          value: 'MANUAL',
          label: 'Nguồn thủ công'
        }
      ],
      statusList: [
        {
          value: 'NOT_SEEN',
          label: 'Chưa xem'
        },
        {
          value: 'VERIFICATION',
          label: 'Xác minh sự kiện'
        },
        {
          value: 'PROCESSING',
          label: 'Đang xử lý'
        },
        {
          value: 'PROCESSED',
          label: 'Đã xử lý'
        },
        {
          value: 'WRONG',
          label: 'Báo sai'
        }
      ]
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      if (this.searchEvent && this.searchEvent.fromDate && this.searchEvent.toDate) {
        this.filter.date = [this.searchEvent.fromDate, this.searchEvent.toDate]
      }
      if (this.searchEvent && this.searchEvent.eventType) {
        this.filter.eventType = this.searchEvent.eventType
      }
      if (this.searchEvent && this.searchEvent.objectType) {
        this.filter.objectType = this.searchEvent.objectType
      }
      if (this.searchEvent && this.searchEvent.sourceType) {
        this.filter.sourceType = [this.searchEvent.sourceType]
      }
      if (this.searchEvent && this.searchEvent.status) {
        this.filter.status = this.searchEvent.status
      }
      console.log('filter   ', this.filter)
      console.log('search   ', this.searchEvent)
    },
    search() {
      this.$refs.formSearch.validate((valid) => {
        if (valid) {
          const queryCustom = {
            startDate: '',
            endDate: '',
            status: this.filter.status ? this.filter.status : [],
            objectType: this.filter.objectType ? this.filter.objectType : [],
            sourceType: this.filter.sourceType ? this.filter.sourceType : [],
            eventType: this.filter.eventType ? this.filter.eventType : []
          }
          if (this.filter.date) {
            const startDate = moment(this.filter.date[0]).format(
              'YYYY-MM-DD HH:mm:ss'
            )
            const endDate = moment(this.filter.date[1]).format(
              'YYYY-MM-DD HH:mm:ss'
            )
            queryCustom.startDate = startDate
            queryCustom.endDate = endDate
          }
          if (this.filter.eventType && this.filter.eventType.length > 0) {
            queryCustom.eventType = this.filter.eventType
          } else {
            queryCustom.eventType = []
          }
          if (this.filter.sourceType && this.filter.sourceType.length > 0) {
            queryCustom.sourceType = this.filter.sourceType
          } else {
            queryCustom.sourceType = []
          }
          if (this.filter.objectType && this.filter.objectType.length > 0) {
            queryCustom.objectType = this.filter.objectType
          } else {
            queryCustom.objectType = []
          }
          if (this.filter.status && this.filter.status.length > 0) {
            queryCustom.status = this.filter.status
          } else {
            queryCustom.status = []
          }
          this.visible = false
          this.$emit('clicked', queryCustom)
        } else {
          return false
        }
      })
    },
    refresh() {
      this.eventHandMade = false
      this.filter = {
        date: [
          moment().subtract(3, 'day').startOf('day').format('YYYY-MM-DD HH:mm:ss'),
          moment().endOf('day').format('YYYY-MM-DD HH:mm:ss')
        ],
        objectType: [],
        eventType: [],
        sourceType: [],
        status: []
      }
    }
  }
}
</script>
  <style type="text/css" lang="scss">
  .search-template {
    .el-popover__reference {
      background: initial;
      border: initial;
      cursor: pointer;
      margin-left: 12px;
      position: relative;
      top: 8px;
    }
  }

  .search-form-content {
    .el-form-item__label {
      width: 100%;
      text-align: left;
    }

    .el-date-editor,
    .el-select {
      width: 100%;
    }

    .el-form-item {
      margin-bottom: 8px;
    }

    .action {
      margin-top: 10px;
      margin-bottom: 20px;

      .el-button--info {
        border-radius: 4px;
        background: #26c6da;
        color: white;
        border: 1px solid #26c6da;
        font-size: 16px;
        font-weight: 500;
        width: 147px;
      }

      .el-button--primary {
        border-radius: 4px;
        background: #1f7ff5;
        color: white;
        border: 1px solid #1f7ff5;
        font-size: 16px;
        font-weight: 500;
        width: 147px;
        float: right;
      }
    }
  }
  </style>

