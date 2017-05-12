/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities;

import java.util.List;

import com.dell.isg.smi.commons.utilities.model.PagedResult;
import com.dell.isg.smi.commons.utilities.model.Pages;
import com.dell.isg.smi.commons.utilities.model.Pagination;

/**
 * @author jian.yang
 *
 */
public final class PaginationUtils {

    private static final Pages ZERO_PAGE = new Pages();
    private static final Pagination ZERO_PAGINATION = new Pagination();
    public static final PagedResult ZERO_RESULT = new PagedResult();

    static {
        ZERO_RESULT.setPages(ZERO_PAGE);
        ZERO_RESULT.setPagination(ZERO_PAGINATION);
    }


    private PaginationUtils() {
    }


    private static final void checkData(final List<?> data) {
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
    }


    /**
     * Check total.
     *
     * @param total the total
     */
    public static final void checkTotal(final long total) {
        if (total < 0) {
            throw new IllegalArgumentException("total");
        }
    }


    /**
     * Check offset.
     *
     * @param offset the offset
     */
    public static final void checkOffset(final long offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset");
        }
    }


    /**
     * Check limit.
     *
     * @param limit the limit
     */
    public static final void checkLimit(final long limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit");
        }
    }


    private static final Pages getPages(final long total, final long offset, final long limit) {

        // ZERO pages
        if (total == 0) {
            return ZERO_PAGE;
        }

        final Pages pages = new Pages();

        long currentPage;
        long nextPage;
        long prevPage;
        long totalPage;

        totalPage = total / limit;
        if (total % limit != 0) {
            totalPage++;
        }

        currentPage = offset / limit + 1;

        if (offset % limit != 0) {
            totalPage++;
            currentPage++;
        }

        if (currentPage == 1) {
            prevPage = -1;
        } else {
            prevPage = currentPage - 1;
        }

        if (currentPage == totalPage) {
            nextPage = -1;
        } else {
            nextPage = currentPage + 1;
        }

        pages.setCurrent(currentPage);
        pages.setNext(nextPage);
        pages.setPrevious(prevPage);
        pages.setTotal(totalPage);

        return pages;
    }


    private static final Pagination getPagination(final long total, final long offset, final long limit) {

        final Pagination pagination = new Pagination();

        pagination.setTotal(total);
        pagination.setOffset(offset);
        pagination.setLimit(limit);

        return pagination;
    }


    /**
     * Generate a PagedResult object
     *
     * @param data the list of data
     * @param total the total size of the whole data set, NOT the size of the data
     * @param offset the offset of this page
     * @param limit the size of this page
     *
     * @return the PagedResult generated with given information.
     */
    public static final PagedResult paginate(final List<?> data, final long total, final long offset, final long limit) {

        // ZERO pages
        if (total == 0) {
            return ZERO_RESULT;
        }

        checkData(data);
        checkTotal(total);
        checkOffset(offset);
        checkLimit(limit);

        final PagedResult pagedResult = new PagedResult();
        pagedResult.getData().addAll(data);

        final Pagination pagination = getPagination(total, offset, limit);
        pagedResult.setPagination(pagination);

        final Pages pages = getPages(total, offset, limit);
        pagedResult.setPages(pages);

        return pagedResult;
    }


   // public static void checkBoundaries(long offset, long limit, long total) {

   //     if (limit > total) {
   //          RuntimeCoreException rce = new RuntimeCoreException(EnumErrorCode.ENUM_INPUT_EXCEEDED_TOTAL);
   //          rce.addAttribute("limit");
   //          throw rce;
    //    }

     //   if (offset > total) {
     //       RuntimeCoreException rce = new RuntimeCoreException(EnumErrorCode.ENUM_INPUT_EXCEEDED_TOTAL);
     //       rce.addAttribute("offset");
      //      throw rce;
      //  }

    //}
}
