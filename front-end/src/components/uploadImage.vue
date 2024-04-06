<template>
  <div class="upload-image">
    <el-upload
      action="#"
      list-type="picture-card"
      :on-error="error"
      :on-success="success"
      :file-list="fileList"
      multiple
      :on-exceed="handleExceed"
      :on-change="handleUploadImage"
      :limit="limit"
      :auto-upload="false"
    >
      <i slot="default" class="el-icon-plus" />
      <div slot="file" slot-scope="{file}">
        <img
          class="el-upload-list__item-thumbnail"
          :src="file.url"
          alt=""
        >
        <span class="el-upload-list__item-actions">
          <span
            v-if="!disabled&&!file.isCam"
            class="el-upload-list__item-delete"
            @click="handleRemove(file)"
          >
            <i class="el-icon-delete" />
          </span>
        </span>
      </div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: {
    limit: {
      type: Number,
      default: 10
    },
    listImage: {
      type: Array,
      default: function() {
        return []
      }
    }
  },
  data() {
    return {
      dialogImageUrl: '',
      dialogVisible: false,
      flagNoimage: true,
      fileList: [],
      disabled: false
    }
  },
  created() {
    const fileList = []
    let item = {}
    this.listImage.forEach(function(val) {
      item = {}
      if (val.startsWith('http')) {
        item.url = val
      } else {
        item.url = process.env.VUE_APP_URL_IMAGE + val
      }
      if (val.search('upload') === -1) {
        item.isCam = true
        console.log(item.isCam)
      }
      fileList.push(item)
    })
    if (this.listImage.length > 0) this.flagNoimage = false
    this.fileList = fileList
  },
  methods: {
    handleRemove(file) {
      this.$emit('removeUploadImage', file)
      this.fileList = this.fileList.filter(function(val) {
        return val !== file
      })
    },
    error(_) {
    },
    success(response, file, fileList) {
      this.$emit('uploadImage', response.fileName)
      this.flagNoimage = false
    },
    beforeUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isPNG = file.type === 'image/png'
      if (!isJPG && !isPNG) {
        this.$message.error('Định dạng file không được chấp nhận, vui lòng chọn ảnh.')
        return false
      }
      const limit = 1
      const isLimit = file.size / 1024 / 1024 <= limit
      if (!isLimit) {
        this.$message.error('Vui lòng chọn file tối đa ' + limit + 'M.')
        return false
      }
      return true
    },
    handleExceed(files, fileList) {
      this.$message.warning('Tối đa ' + this.limit + ' ảnh cho phép')
    },

    handleUploadImage(file, fileList) {
      const isJPG = file.raw.type === 'image/jpeg'
      const isPNG = file.raw.type === 'image/png'
      const isLimit = file.size / 1024 / 1024 <= 10
      if (!isJPG && !isPNG) {
        this.$message.error('Định dạng file không được chấp nhận, vui lòng chọn ảnh.')
        fileList.pop()
      } else if (!isLimit) {
        this.$message.error('Vui lòng chọn file tối đa ' + 10 + 'MB.')
        fileList.pop()
      }
      this.$emit('getListFile', fileList)
      this.fileList = fileList
    },

    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleDownload(file) {
      console.log(file)
    }
  }
}
</script>
<style type="text/css" lang="scss">
.el-upload-list--picture-card .el-upload-list__item {
  line-height: 148px;
  background: black;
}

.no-image {
  margin-bottom: 10px;
}
</style>
